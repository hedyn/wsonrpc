package net.apexes.wsonrpc.demo.client;

import net.apexes.wsonrpc.demo.api.CallPosService;
import net.apexes.wsonrpc.demo.api.User;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class CallPosServiceImpl implements CallPosService {

    @Override
    public void notifyMessage(String message) throws Exception {
        System.out.println("Message From Server: " + message);
    }

    @Override
    public User getPosUser(String userId) throws Exception {
        User user = new User();
        user.setUsername(userId);
        user.setPassword("123456");
        return user;
    }

}
