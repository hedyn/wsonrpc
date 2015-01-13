package net.apexes.wsonrpc.demo.api;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface LoginService {
    
    User login(String username, String password);
    
    User login(User user);

    String login1(String username, String password);

    String login2(String username, String password);

    String login3(String username, String password);

    String login4(String username, String password);
}
