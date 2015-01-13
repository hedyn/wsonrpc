package net.apexes.wsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcError {
    
    /**
     * Parse Error constant
     */
    public static final JsonRpcError PARSE_ERROR = new JsonRpcError(-32700, "Parse Error");
    
    /**
     * Invalid Request Error constant
     */
    public static final JsonRpcError INVALID_REQUEST = new JsonRpcError(-32600, "Invalid Request");
    
    /**
     * Method Not Found Error constant
     */
    public static final JsonRpcError METHOD_NOT_FOUND = new JsonRpcError(-32601, "Method not found");
    
    /**
     * Invalid Params error constant
     */
    public static final JsonRpcError INVALID_PARAMS = new JsonRpcError(-32602, "Invalid Params");
    
    /**
     * Internal Error constant
     */
    public static final JsonRpcError INTERNAL_ERROR = new JsonRpcError(-32603, "Internal Error");
    
    private final int code;
    
    private final String message;
    
    public JsonRpcError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
