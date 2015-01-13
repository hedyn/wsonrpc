package net.apexes.wsonrpc.message;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class JsonRpcMessage {
    
    public static final String VERSION = "2.0";
    
    private final String jsonrpc;
    
    protected JsonRpcMessage() {
        jsonrpc = VERSION;
    }
    
    public String getJsonrpc() {
        return jsonrpc;
    }
    
    public abstract Type type();
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static enum Type {
        
        NOTIFICATION,
        
        REQUEST,
        
        RESPONSE
        
    }
}
