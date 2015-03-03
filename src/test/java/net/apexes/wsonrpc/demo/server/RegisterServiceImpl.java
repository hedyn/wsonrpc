package net.apexes.wsonrpc.demo.server;

import java.util.Iterator;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.demo.api.RegisterService;
import net.apexes.wsonrpc.demo.api.User;
import net.apexes.wsonrpc.server.Remotes;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class RegisterServiceImpl implements RegisterService {
    
    @Override
    public void registerPos(String posId) throws Exception {
        WsonrpcRemote remote = Remotes.getRemote();
        String sessionId = remote.getSessionId();
        OnlinePosHolder.registerPosId(posId, sessionId);
    }

    @Override
    public User registerUser(User user) throws Exception {
        Iterator<String> it = OnlinePosHolder.getOnlines().iterator();
        if (it.hasNext()) {
            String posId = it.next();
            // 向指定的POS发送通知
            WsonrpcServerDemo.notifyMessage(posId, "call server's registerUser()");
            
            // 从指定的POS获取数据
            User posUser = WsonrpcServerDemo.getUserFromPos(posId, "admin");
            System.out.println("From Client: " + posUser);
        }
        
        user.setLevel(10);
        return user;
    }

}
