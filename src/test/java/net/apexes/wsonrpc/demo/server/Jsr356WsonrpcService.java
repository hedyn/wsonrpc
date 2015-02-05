package net.apexes.wsonrpc.demo.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;
import net.apexes.wsonrpc.server.WsonrpcServerEndpoint;

import org.glassfish.tyrus.core.MaxSessions;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc/{client}")
public class Jsr356WsonrpcService implements ExceptionProcessor {
    
    private final WsonrpcServerEndpoint endpoint;

    public Jsr356WsonrpcService() {
        endpoint = new WsonrpcServerEndpoint(Executors.newCachedThreadPool());
        endpoint.setExceptionProcessor(this);
        endpoint.register(new LoginServiceImpl());
    }

    @Override
    public void onError(Throwable error, Object... params) {
        if (params != null) {
            System.err.println(Arrays.toString(params));
        }
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
        endpoint.onOpen(new WebSocketSessionAdapter(session));
//        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
//        System.out.println("onClose: "+session);
        endpoint.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        endpoint.onMessage(session.getId(), buffer);
    }
}
