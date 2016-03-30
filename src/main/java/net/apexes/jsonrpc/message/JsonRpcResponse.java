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
public abstract class JsonRpcResponse extends JsonRpcMessage {

    private final String id;
    
    protected JsonRpcResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
}
