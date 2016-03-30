/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc.message;


/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcResponseError extends JsonRpcResponse {

    private final JsonRpcError error;
    
    public JsonRpcResponseError(String id, JsonRpcError error) {
        super(id);
        this.error = error;
    }

    public JsonRpcError getError() {
        return error;
    }
}
