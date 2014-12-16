package net.apexes.wsonrpc.demo.client;

import net.apexes.wsonrpc.demo.api.UserService;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class ClientUserServiceImpl implements UserService {

    public String login1(String username, String password) {
        return "client: 1:" + username;
    }

    public String login2(String username, String password) {
        return "client: 2:" + username;
    }

    public String login3(String username, String password) {
        return "client: 3:" + username;
    }

    @Override
    public String login4(String username, String password) {
        return "client: 4:" + username;
    }

}
