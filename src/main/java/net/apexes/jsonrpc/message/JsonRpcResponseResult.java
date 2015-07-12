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
public class JsonRpcResponseResult extends JsonRpcResponse {

    private final Object result;
    
    public JsonRpcResponseResult(String id, Object result) {
        super(id);
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
}
