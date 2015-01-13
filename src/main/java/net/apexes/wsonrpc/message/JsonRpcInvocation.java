package net.apexes.wsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class JsonRpcInvocation extends JsonRpcMessage {
    
    private final String method;
    
    private final Object params;
    
    protected JsonRpcInvocation(String method) {
        this(method, null);
    }
    
    protected JsonRpcInvocation(String method, Object params) {
        this.method = method;
        this.params = params;
    }
    
    public String getMethod() {
        return method;
    }
    
    public Object getParams() {
        return params;
    }

}
