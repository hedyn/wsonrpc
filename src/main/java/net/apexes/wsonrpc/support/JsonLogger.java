package net.apexes.wsonrpc.support;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonLogger {
    
    void onRead(String json);
    
    void onWrite(String json);

}
