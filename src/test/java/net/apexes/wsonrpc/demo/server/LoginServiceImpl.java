package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcServer;
import net.apexes.wsonrpc.demo.api.CallClientService;
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.demo.api.User;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class LoginServiceImpl implements LoginService {

    @Override
    public User login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setLevel(10);
        return user;
    }

    public String login1(String username, String password) {
        System.out.println("login1(" + username + ", " + password + ")");
        callClient(username, password);
        return "[" + username + "] call #login1 succeed!";
    }

    public String login2(String username, String password) {
        System.out.println("login2(" + username + ", " + password + ")");
        return "[" + username + "] call #login2 succeed!";
    }

    public String login3(String username, String password) {
        System.out.println("login3(" + username + ", " + password + ")");
        return "[" + username + "] call #login3 succeed!";
    }

    public String login4(String username, String password) {
        System.out.println("login4(" + username + ", " + password + ")");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }
        return "[" + username + "] call #login4 succeed!";
    }

    private void callClient(final String username, final String password) {
        WsonrpcRemote remote = WsonrpcServer.Manager.getRemote();
        if (remote != null) {
            CallClientService callClientSrv = WsonrpcRemote.Executor.createProxy(remote, CallClientService.class, "callClientService");
            System.out.println(callClientSrv.callClient("The username is " + username));
        }
    }
}
