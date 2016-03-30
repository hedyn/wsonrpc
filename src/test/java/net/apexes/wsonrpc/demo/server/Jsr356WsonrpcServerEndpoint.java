package net.apexes.wsonrpc.demo.server;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.core.MaxSessions;

import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;
import net.apexes.wsonrpc.server.WsonrpcServerEndpoint;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc/{client}")
public class Jsr356WsonrpcServerEndpoint extends WsonrpcServerEndpoint implements ErrorProcessor {
    

    public Jsr356WsonrpcServerEndpoint() {
        super(Executors.newCachedThreadPool());
        setErrorProcessor(this);
        getServiceRegistry()
            .register(new LoginServiceImpl())
            .register(new RegisterServiceImpl());
    }

    @Override
    public void onError(String sessionId, Throwable error) {
        error.printStackTrace();
    }
    
    @OnOpen
    public void onOpen(Session session, @PathParam("client") String client) {
//        System.out.println("client="+client + ", session=" + session);
//        if ("1".equals(client)) {
//            try {
//                session.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
        super.onOpen(new WebSocketSessionAdapter(session));
//        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
//        System.out.println("onClose: "+session);
        super.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        super.onMessage(session.getId(), buffer);
    }
}
