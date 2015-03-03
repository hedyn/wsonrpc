package net.apexes.wsonrpc.demo.api;

/**
 * 供Server调用的Client端API
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface CallPosService {
    
    void notifyMessage(String message) throws Exception;

    User getPosUser(String userId) throws Exception;
}
