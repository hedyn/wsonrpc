package net.apexes.wsonrpc.message;

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
    
    private final int code;
    
    private final String message;
    
    private final Object data;
    
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

    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }

}
