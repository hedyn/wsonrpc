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
        return new JsonRpcError(-32700, "Parse error", getStackTrace(t));
    }
    
    public static JsonRpcError invalidRequestError(Throwable t) {
        return new JsonRpcError(-32600, "Invalid Request", getStackTrace(t));
    }
    
    public static JsonRpcError methodNotFoundError(Throwable t) {
        return new JsonRpcError(-32601, "Method not found", getStackTrace(t));
    }
    
    public static JsonRpcError invalidParamsError(Throwable t) {
        return new JsonRpcError(-32602, "Invalid params", getStackTrace(t));
    }
    
    public static JsonRpcError internalError(Throwable t) {
        return new JsonRpcError(-32603, "Internal error", getStackTrace(t));
    }
    
    public static JsonRpcError serverError(int index, String msg, Throwable t) {
        return new JsonRpcError(-32000 - index, msg, getStackTrace(t));
    }

    /**
     *
     * @param t
     * @return
     */
    static String getStackTrace(Throwable t) {
        if (t == null) {
            return null;
        }
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);
        t.printStackTrace(writer);
        writer.close();
        return strWriter.toString();
    }

}
