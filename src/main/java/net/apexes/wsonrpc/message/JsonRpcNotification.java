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
public class JsonRpcNotification extends JsonRpcInvocation {

    public JsonRpcNotification(String method) {
        super(method);
    }
    
    public JsonRpcNotification(String method, Object params) {
        super(method, params);
    }

    @Override
    public Type type() {
        return Type.NOTIFICATION;
    }

}
