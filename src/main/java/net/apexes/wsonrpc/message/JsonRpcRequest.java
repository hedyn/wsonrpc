package net.apexes.wsonrpc.message;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcRequest extends JsonRpcInvocation {
    
    private final String id;
    
    public JsonRpcRequest(String id, String method) {
        this(id, method, null);
    }
    
    public JsonRpcRequest(String id, String method, Object params) {
        super(method, params);
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public Type type() {
        return Type.REQUEST;
    }

}
