package net.apexes.wsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcRequest extends JsonRpcMessage {
    
    private final String method;
    
    private final Object params;
    
    private final String id;
    
    public JsonRpcRequest(String id, String method) {
        this(id, method, null);
    }
    
    public JsonRpcRequest(String id, String method, Object params) {
        this.method = method;
        this.params = params;
        this.id = id;
    }
    
    public String getMethod() {
        return method;
    }

    public Object getParams() {
        return params;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public Type type() {
        return Type.REQUEST;
    }

}
