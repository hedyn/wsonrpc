/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;

import net.apexes.jsonrpc.message.JsonRpcError;
import net.apexes.jsonrpc.message.JsonRpcMessage;
import net.apexes.jsonrpc.message.JsonRpcNotification;
import net.apexes.jsonrpc.message.JsonRpcRequest;
import net.apexes.jsonrpc.message.JsonRpcResponseError;
import net.apexes.jsonrpc.message.JsonRpcResponseResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class GsonJsonContext extends AbstractJsonContext {
    
    private final Gson gson;
    
    public GsonJsonContext() {
        this(new GsonBuilder().setDateFormat(DEFAUAL_DATE_TIME_FORMAT).create());
    }
    
    public GsonJsonContext(Gson gson) {
        this.gson = gson;
    }
    
    @Override
    public JsonRpcMessage read(InputStream ips) throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(new InputStreamReader(ips));
        if (getLogger() != null) {
            getLogger().onRead(jsonElement.toString());
        }
        if (!jsonElement.isJsonObject()) {
            throw new Exception("Invalid data.");
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
                return new JsonRpcResponseResult(id, result);
            }
            
            if (jsonObject.has("error")) {
                JsonElement errorElement = jsonObject.get("error");
                if (!errorElement.isJsonObject()) {
                    throw new Exception("Invalid data.");
                }
                JsonObject errorObject = errorElement.getAsJsonObject();
                JsonElement codeElement = errorObject.get("code");
                JsonElement messageElement = errorObject.get("message");
                int code = codeElement.getAsInt();
                String message = messageElement.getAsString();
                JsonRpcError error = new JsonRpcError(code, message);
                return new JsonRpcResponseError(id, error);
            }
        }
        throw new Exception("Invalid data.");
    }

    @Override
    public void write(JsonRpcMessage message, OutputStream ops) throws Exception {
        String json = gson.toJson(message);
        if (getLogger() != null) {
            getLogger().onWrite(json);
        }
        ops.write(json.getBytes());
        ops.flush();
    }

    @Override
    public JsonParams convertParams(Object params) {
        return new JsonElementParams((JsonElement)params);
    }

    @Override
    public <E> E convert(Object node, Type type) {
        return gson.fromJson((JsonElement)node, type);
    }

    @Override
    public boolean isMatchingType(Object node, Class<?> classType) {
        JsonElement element = (JsonElement) node;
        if (element.isJsonNull()) {
            return true;
        }
        try  {
            Object obj = gson.fromJson(element, classType);
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
    private static class JsonElementParams implements JsonParams {
        
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
                throw new IndexOutOfBoundsException("size is " + size + ", index is " + index);
            }
            if (size == 1) {
                return element;
            }
            return element.getAsJsonArray().get(index);
        }
        
    }

}
