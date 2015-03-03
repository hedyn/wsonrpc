package net.apexes.wsonrpc.demo.api;

/**
 * 供Client调用的Server端API
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface RegisterService {
    
    void registerPos(String posId) throws Exception;

    User registerUser(User user) throws Exception;
}
