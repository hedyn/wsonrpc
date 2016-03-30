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
public class JsonRpcRequest extends JsonRpcNotification {
    
    private final String id;
    
    public JsonRpcRequest(String id, String method, Object params) {
        super(method, params);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
