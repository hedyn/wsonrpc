/*
 * Copyright (C) 2015, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcResponse extends JsonRpcMessage {
    
    private final String id;
    
    private final Object result;
    
    private final JsonRpcError error;
    
    public JsonRpcResponse(String id, Object result) {
        this.id = id;
        this.result = result;
        this.error = null;
    }
    
    public JsonRpcResponse(String id, JsonRpcError error) {
        this.id = id;
        this.result = null;
        this.error = error;
    }
    
    public String getId() {
        return id;
    }
    
    public Object getResult() {
        return result;
    }
    
    public JsonRpcError getError() {
        return error;
    }
    
    @Override
    public Type type() {
        return Type.RESPONSE;
    }

}
