/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;

import net.apexes.jsonrpc.message.JsonRpcError;
import net.apexes.jsonrpc.message.JsonRpcMessage;
import net.apexes.jsonrpc.message.JsonRpcNotification;
import net.apexes.jsonrpc.message.JsonRpcRequest;
import net.apexes.jsonrpc.message.JsonRpcResponseError;
import net.apexes.jsonrpc.message.JsonRpcResponseResult;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JacksonJsonContext extends AbstractJsonContext {
    
    private final ObjectMapper objectMapper;

    public JacksonJsonContext() {
        this(new NonNullObjectMapper());
    }
            
    public JacksonJsonContext(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    @Override
    public JsonRpcMessage read(InputStream ips) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(new NoCloseInputStream(ips));
        if (getLogger() != null) {
            getLogger().onRead(jsonNode.toString());
        }
        if (!jsonNode.isObject()) {
            throw new Exception("Invalid data.");
        }
        
        ObjectNode objectNode = ObjectNode.class.cast(jsonNode);
        
        String id = null;
        JsonNode idNode = objectNode.get("id");
        if (idNode != null && idNode.isTextual()) {
            id = idNode.textValue();
        }
        
        if (objectNode.has("method")) {
            JsonNode methodNode = objectNode.get("method");
            if (!methodNode.isTextual()) {
                throw new Exception("Invalid Request.");
            }
            String method = methodNode.textValue();
            Object params = null;
            if (objectNode.has("params")) {
                params = objectNode.get("params");
            }
            if (id == null) {
                return new JsonRpcNotification(method, params);
            }
            return new JsonRpcRequest(id, method, params);
        }
        
        if (id != null) {
            if (objectNode.has("result")) {
                Object result = objectNode.get("result");
                return new JsonRpcResponseResult(id, result);
            }
            
            if (objectNode.has("error")) {
                JsonNode errorNode = objectNode.get("error");
                JsonNode codeNode = errorNode.get("code");
                JsonNode messageNode = errorNode.get("message");
                int code = codeNode.asInt();
                String message = messageNode.asText();
                JsonRpcError error = new JsonRpcError(code, message);
                return new JsonRpcResponseError(id, error);
            }
        }
        
        throw new Exception("Invalid data.");
    }

    @Override
    public void write(JsonRpcMessage message, OutputStream ops) throws Exception {
        if (getLogger() != null) {
            ByteArrayOutputStream bops = new ByteArrayOutputStream();
            objectMapper.writeValue(bops, message);
            
            byte[] bytes = bops.toByteArray();
            String json = new String(bytes);
            getLogger().onWrite(json);
            
            ops.write(bytes);
        } else {
            objectMapper.writeValue(ops, message);
        }
    }

    @Override
    public JsonParams convertParams(Object params) {
        return new JsonNodeParams((JsonNode)params);
    }
    
    @Override
    public <E> E convert(Object node, Type type) throws Exception {
        JsonParser jsonParser = objectMapper.treeAsTokens((JsonNode)node);
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        return objectMapper.readValue(jsonParser, javaType);
    }

    @Override
    public boolean isMatchingType(Object node, Class<?> classType) {
        JsonNode jsonNode = (JsonNode) node;
        if (jsonNode.isNull()) {
            return true;

        } else if (jsonNode.isTextual()) {
            return String.class.isAssignableFrom(classType);

        } else if (jsonNode.isNumber()) {
            return Number.class.isAssignableFrom(classType)
                || short.class.isAssignableFrom(classType)
                || int.class.isAssignableFrom(classType)
                || long.class.isAssignableFrom(classType)
                || float.class.isAssignableFrom(classType)
                || double.class.isAssignableFrom(classType);

        } else if (jsonNode.isArray() && classType.isArray()) {
            return (jsonNode.size()>0)
                ? isMatchingType(jsonNode.get(0), classType.getComponentType())
                : false;

        } else if (jsonNode.isArray()) {
            return classType.isArray() || Collection.class.isAssignableFrom(classType);

        } else if (jsonNode.isBinary()) {
            return byte[].class.isAssignableFrom(classType)
                || Byte[].class.isAssignableFrom(classType)
                || char[].class.isAssignableFrom(classType)
                || Character[].class.isAssignableFrom(classType);

        } else if (jsonNode.isBoolean()) {
            return boolean.class.isAssignableFrom(classType)
                || Boolean.class.isAssignableFrom(classType);

        } else if (jsonNode.isObject() || jsonNode.isPojo()) {
            return !classType.isPrimitive()
                && !String.class.isAssignableFrom(classType)
                && !Number.class.isAssignableFrom(classType)
                && !Boolean.class.isAssignableFrom(classType);
        }

        // not sure if it's a matching type
        return false;
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class JsonNodeParams implements JsonParams {
        
        private final JsonNode node;
        private final int size;
        
        JsonNodeParams(JsonNode node) {
            this.node = node;
            if (node == null) {
                size = 0;
            } else if (node.isArray()) {
                size = node.size();
            } else {
                size = 1;
            }
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public JsonNode get(int index) {
            if (index >= size) {
                throw new java.lang.IndexOutOfBoundsException("size is " + size + ", index is " + index);
            }
            if (size == 1) {
                return node;
            }
            return node.get(index);
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class NonNullObjectMapper extends ObjectMapper {
        
        private static final long serialVersionUID = 1L;
        
        public NonNullObjectMapper() {
            setSerializationInclusion(Include.NON_NULL);
            setDateFormat(new SimpleDateFormat(AbstractJsonContext.DEFAUAL_DATE_TIME_FORMAT));
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class NoCloseInputStream extends InputStream {

        private InputStream ips;
        private boolean closeAttempted = false;

        public NoCloseInputStream(InputStream ips) {
            this.ips = ips;
        }

        @Override
        public int read() throws IOException {
            return this.ips.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.ips.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.ips.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.ips.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.ips.available();
        }

        @Override
        public void close() throws IOException {
            closeAttempted = true;
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.ips.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.ips.reset();
        }

        @Override
        public boolean markSupported() {
            return this.ips.markSupported();
        }

        /**
         * @return the closeAttempted
         */
        public boolean wasCloseAttempted() {
            return closeAttempted;
        }

    }
}
