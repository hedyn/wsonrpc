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
public abstract class JsonRpcMessage {
    
    private String jsonrpc = "2.0";
    
    public String getJsonrpc() {
        return jsonrpc;
    }
}
