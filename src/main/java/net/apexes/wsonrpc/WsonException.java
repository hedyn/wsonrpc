package net.apexes.wsonrpc;


/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final int code;
    
    private final Object data;
    
    public WsonException(String message) {
        this(0, message, null, null);
    }
    
    public WsonException(String message, Throwable cause) {
        this(0, message, null, cause);
    }
    
    public WsonException(int code, String message) {
        this(code, message, null, null);
    }
    
    public WsonException(int code, String message, Throwable cause) {
        this(code, message, null, cause);
    }
    
    public WsonException(int code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }
    
    public int getCode() {
        return code;
    }
    
    public Object getData() {
        return data;
    }

}
