package net.apexes.wsonrpc.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;

import net.apexes.wsonrpc.WsonException;
import net.apexes.wsonrpc.message.JsonRpcError;
import net.apexes.wsonrpc.message.JsonRpcMessage;
import net.apexes.wsonrpc.message.JsonRpcNotification;
import net.apexes.wsonrpc.message.JsonRpcRequest;
import net.apexes.wsonrpc.message.JsonRpcResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class GsonJsonHandler extends AbstractJsonHandler<JsonElement> {
    
    private final Gson gson;
    
    public GsonJsonHandler() {
        this(new Gson());
    }
    
    public GsonJsonHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public JsonRpcMessage read(InputStream ips) throws IOException, WsonException {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader(ips));
            if (!jsonElement.isJsonObject()) {
                throw new WsonException("Invalid WSON-RPC data.");
            }
            
            JsonObject jsonObject = JsonObject.class.cast(jsonElement);
            
            String id = null;
            JsonElement idElement = jsonObject.get("id");
            if (idElement != null) {
                id = idElement.getAsString();
            }
            
            if (jsonObject.has("method")) {
                JsonElement methodElement = jsonObject.get("method");
                String method = methodElement.getAsString();
                Object params = null;
                if (jsonObject.has("params")) {
                    params = jsonObject.get("params");
                }
                if (id == null) {
                    return new JsonRpcNotification(method, params);
                }
                return new JsonRpcRequest(id, method, params);
            }
            
            if (id != null) {
                if (jsonObject.has("result")) {
                    Object result = jsonObject.get("result");
                    return new JsonRpcResponse(id, result);
                }
                
                if (jsonObject.has("error")) {
                    JsonElement errorElement = jsonObject.get("error");
                    if (!errorElement.isJsonObject()) {
                        throw new WsonException("Invalid WSON-RPC data.");
                    }
                    JsonObject errorObject = errorElement.getAsJsonObject();
                    JsonElement codeElement = errorObject.get("code");
                    JsonElement messageElement = errorObject.get("message");
                    int code = codeElement.getAsInt();
                    String message = messageElement.getAsString();
                    JsonRpcError error = new JsonRpcError(code, message);
                    return new JsonRpcResponse(id, error);
                }
            }
            
            throw new WsonException("Invalid WSON-RPC data.");
        } catch (Exception ex) {
            throw new WsonException(ex.getMessage(), ex.getCause());
        }
        
    }

    @Override
    public void write(JsonRpcMessage message, OutputStream ops) throws IOException, WsonException {
        try {
            String json = gson.toJson(message);
            ops.write(json.getBytes());
            ops.flush();
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WsonException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    protected Object convertObject(JsonElement node, Type type) throws Exception {
        return gson.fromJson(node, type);
    }

    @Override
    protected AbstractJsonHandler.IParams<JsonElement> convertParams(Object params) {
        return new JsonElementParams((JsonElement)params);
    }

    @Override
    protected boolean isMatchingType(JsonElement node, Class<?> classType) {
        if (node.isJsonNull()) {
            return true;
        }
        try  {
            Object obj = gson.fromJson(node, classType);
            return obj != null;
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class JsonElementParams implements IParams<JsonElement> {
        
        private final JsonElement element;
        private final int size;
        
        JsonElementParams(JsonElement element) {
            this.element = element;
            if (element == null) {
                size = 0;
            } else if (element.isJsonArray()) {
                size = element.getAsJsonArray().size();
                
            } else {
                size = 1;
            }
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public JsonElement get(int index) {
            if (index >= size) {
                throw new java.lang.IndexOutOfBoundsException("size is " + size + ", index is " + index);
            }
            if (size == 1) {
                return element;
            }
            return element.getAsJsonArray().get(index);
        }
        
    }

}
