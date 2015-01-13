package net.apexes.wsonrpc.demo.api;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface CallClientService {
    
    String callClient(String msg);
    
    String[] callClient(User user);

}
