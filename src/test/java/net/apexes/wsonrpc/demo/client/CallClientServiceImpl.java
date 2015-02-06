package net.apexes.wsonrpc.demo.client;

import net.apexes.wsonrpc.demo.api.CallClientService;
import net.apexes.wsonrpc.demo.api.User;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class CallClientServiceImpl implements CallClientService {
    
    @Override
    public String callClient(String msg) {
        String s = "Client result: " + msg;
//        System.err.println(s);
        return s;
    }
    
    @Override
    public String[] callClient(User user) {
        String[] results = new String[2];
        results[0] = user.getUsername();
        results[1] = user.getPassword();
//        System.err.println("user: " + user);
        return results;
    }

}
