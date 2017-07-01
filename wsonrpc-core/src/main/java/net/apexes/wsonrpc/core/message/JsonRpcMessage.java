/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core.message;

import net.apexes.wsonrpc.core.JsonException;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.JsonImplementor.Node;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class JsonRpcMessage {
    
    protected final String id;
    
    protected JsonRpcMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    /**
     * 
     * @param jsonImpl
     * @return
     * @throws Exception
     */
    public abstract String toJson(JsonImplementor jsonImpl) throws JsonException;
            
    /**
     * 
     * @param jsonImpl
     * @param json
     * @return
     */
    public static JsonRpcMessage of(JsonImplementor jsonImpl, String json) throws JsonException {
        Node node = jsonImpl.fromJson(json);
        
        String id = null;
        if (node.has("id")) {
            id = node.getString("id");
        }
        
        if (node.has("method")) {
            String method = node.getString("method");
            Node[] params;
            if (node.has("params")) {
                params = node.getArray("params");
            } else {
                params = new Node[0];
            }
            return new JsonRpcRequest(id, method, params);
        }
        
        if (node.has("error")) {
            Node error = node.get("error");
            Integer code = error.getInteger("code");
            String message = error.getString("message");
            String data = null;
            if (error.has("data")) {
                data = error.getString("data");
            }
            return new JsonRpcResponse(id, new JsonRpcError(code, message, data));
        }
                    
        if (node.has("result")) {
            Node result = node.get("result");
            return new JsonRpcResponse(id, result);
        }
        return null;
    }

}
