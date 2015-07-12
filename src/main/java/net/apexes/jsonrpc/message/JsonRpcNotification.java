/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc.message;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcNotification extends JsonRpcMessage {
    
    private final String method;
    
    private final Object params;
    
    public JsonRpcNotification(String method, Object params) {
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public Object getParams() {
        return params;
    }
    
}
