/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.core.message.JsonRpcError;

import java.io.PrintStream;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class RemoteException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final JsonRpcError error;

    public RemoteException(JsonRpcError error) {
        super(formatMessage(error));
        this.error = error;
        this.printStackTrace();
    }

    public JsonRpcError getJsonRpcError() {
        return error;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (error.getData() != null) {
            String data = error.getData();
            data = data.replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
            s.print(data);
        }
    }

    private static String formatMessage(JsonRpcError error) {
        StringBuilder str = new StringBuilder();
        if (error.getCode() != null) {
            str.append("[").append(error.getCode()).append("]");
        }
        if (error.getMessage() != null) {
            str.append(" ");
            str.append(error.getMessage());
        }
        return str.toString();
    }

}
