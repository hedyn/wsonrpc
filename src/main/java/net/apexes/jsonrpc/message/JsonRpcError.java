/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcError {
    
    /**
     * 
     * @param data
     * @return
     */
    public static final JsonRpcError createParseError(Object data) {
        return new JsonRpcError(-32700, "Parse Error", data);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static final JsonRpcError createInvalidRequestError(Object data) {
        return new JsonRpcError(-32600, "Invalid Request", data);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static final JsonRpcError createMethodNoFound(Object data) {
        return new JsonRpcError(-32601, "Method not found", data);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static final JsonRpcError createInvalidParamsError(Object data) {
        return new JsonRpcError(-32602, "Invalid Params", data);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    public static final JsonRpcError createInternalError(Object data) {
        return new JsonRpcError(-32603, "Internal Error", data);
    }
    
    private int code;
    
    private String message;
    
    private Object data;
    
    public JsonRpcError() {
    }
    
    public JsonRpcError(int code, String message) {
        this( code, message, null);
    }
    
    public JsonRpcError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
