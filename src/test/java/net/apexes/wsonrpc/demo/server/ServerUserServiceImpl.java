package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcServer;
import net.apexes.wsonrpc.demo.api.UserService;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class ServerUserServiceImpl implements UserService {

    public String login1(String username, String password) {
        System.out.println("login1(" + username + ", " + password + ")");
        callClient(username, password);
        return "Server: 1:" + username;
    }

    public String login2(String username, String password) {
        System.out.println("login2(" + username + ", " + password + ")");
        return "Server: 2:" + username;
    }

    public String login3(String username, String password) {
        System.out.println("login3(" + username + ", " + password + ")");
        return "Server: 3:" + username;
    }

    public String login4(String username, String password) {
        System.out.println("login4(" + username + ", " + password + ")");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }
        return "Server: 4:" + username;
    }

    private void callClient(final String username, final String password) {
        WsonrpcRemote remote = WsonrpcServer.Manager.getRemote();
        if (remote != null) {
            UserService userSrv = WsonrpcRemote.Executor.createProxy(remote, UserService.class, "user");
            System.out.println(userSrv.login1(username, password));
        }
    }
}
