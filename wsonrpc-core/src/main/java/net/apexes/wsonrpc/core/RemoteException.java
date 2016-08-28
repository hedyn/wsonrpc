/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.core.message.JsonRpcError;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class RemoteException extends WsonrpcException {
    private static final long serialVersionUID = 1L;
    
    private final JsonRpcError error;

    public RemoteException(JsonRpcError error) {
        super(format(error));
        this.error = error;
    }

    public JsonRpcError getJsonRpcError() {
        return error;
    }

    private static String format(JsonRpcError error) {
        StringBuilder str = new StringBuilder();
        str.append("jsonrpc error");
        if (error.getCode() != null) {
            str.append("[").append(error.getCode()).append("]");
        }
        str.append(" : ");
        if (error.getMessage() != null) {
            str.append(error.getMessage());
        }
        if (error.getData() != null) {
            str.append("\n");
            str.append("Caused by " + error.getData());
        }
        return str.toString();
    }

}
