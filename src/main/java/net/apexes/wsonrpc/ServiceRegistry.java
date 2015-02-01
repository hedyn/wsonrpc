package net.apexes.wsonrpc;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface ServiceRegistry {

    void register(String name, Object handler);
    
    void register(Object handler);
}
