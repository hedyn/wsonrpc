/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core.message;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcError {
    
    private final Integer code;
    private final String message;
    private final String data;
    
    public JsonRpcError(Integer code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
    
    public static JsonRpcError parseError(Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32700, "Parse error", data);
    }
    
    public static JsonRpcError invalidRequestError(Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32600, "Invalid Request", data);
    }
    
    public static JsonRpcError methodNotFoundError(Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32601, "Method not found", data);
    }
    
    public static JsonRpcError invalidParamsError(Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32602, "Invalid params", data);
    }
    
    public static JsonRpcError internalError(Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32603, "Internal error", data);
    }
    
    public static JsonRpcError serverError(int index, Throwable t) {
        String data = null;
        if (t != null) {
            data = getStackTrace(t);
        }
        return new JsonRpcError(-32000 - index, "Server error", data);
    }
    
    /**
     * 
     * @param t
     * @return
     */
    static String getStackTrace(Throwable t) {
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);
        t.printStackTrace(writer);
        writer.close();
        return strWriter.toString();
    }
}
