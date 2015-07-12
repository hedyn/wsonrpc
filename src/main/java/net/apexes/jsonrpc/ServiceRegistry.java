package net.apexes.jsonrpc;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface ServiceRegistry {

    void register(String name, Object service);
    
    void register(Object service);
}
