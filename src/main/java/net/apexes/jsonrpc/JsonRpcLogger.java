package net.apexes.jsonrpc;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonRpcLogger {
    
    void onRead(String json);
    
    void onWrite(String json);

}
