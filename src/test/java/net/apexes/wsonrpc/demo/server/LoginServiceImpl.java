package net.apexes.wsonrpc.demo.server;

import java.util.Arrays;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.demo.api.CallClientService;
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.demo.api.User;
import net.apexes.wsonrpc.service.WsonrpcService;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
@SuppressWarnings("unused")
public class LoginServiceImpl implements LoginService {

    @Override
    public User login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setLevel(10);
        return user;
    }
    
    @Override
    public User login(User user) {
        callClient(user);
        user.setLevel(user.getLevel()==null?10:12);
        return user;
    }

    public String login1(String username, String password) {
//        System.out.println("login1(" + username + ", " + password + ")");
        callClient(username, password);
        return "[" + username + "] call #login1 succeed!";
    }

    public String login2(String username, String password) {
//        System.out.println("login2(" + username + ", " + password + ")");
        return "[" + username + "] call #login2 succeed!";
    }

    public String login3(String username, String password) {
//        System.out.println("login3(" + username + ", " + password + ")");
        return "[" + username + "] call #login3 succeed!";
    }

    public String login4(String username, String password) {
//        System.out.println("login4(" + username + ", " + password + ")");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }
        return "[" + username + "] call #login4 succeed!";
    }

    private void callClient(final String username, final String password) {
        WsonrpcRemote remote = WsonrpcService.Manager.getRemote();
        if (remote != null) {
            CallClientService callClientSrv = WsonrpcRemote.Executor.createProxy(remote, CallClientService.class, "callClientService");
            String result = callClientSrv.callClient("The username is " + username);
//            System.out.println(result);
        }
    }
    
    private void callClient(final User user) {
        WsonrpcRemote remote = WsonrpcService.Manager.getRemote();
        if (remote != null) {
            CallClientService callClientSrv = WsonrpcRemote.Executor.createProxy(remote, CallClientService.class, "callClientService");
            String[] results = callClientSrv.callClient(user);
//            System.out.println(Arrays.toString(results));
        }
    }
}
