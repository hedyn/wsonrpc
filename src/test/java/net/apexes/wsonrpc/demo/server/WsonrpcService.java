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
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.service.WsonrpcServiceEndpointProxy;

import org.glassfish.tyrus.core.MaxSessions;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc/{client}")
public class WsonrpcService implements ExceptionProcessor {
    
    private final WsonrpcServiceEndpointProxy proxy;

    public WsonrpcService() {
        proxy = new WsonrpcServiceEndpointProxy(Executors.newCachedThreadPool());
        proxy.setExceptionProcessor(this);
        proxy.addService(LoginService.class.getSimpleName(), new LoginServiceImpl());
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
//        System.out.println("client="+client);
//        if ("1".equals(client)) {
//            try {
//                session.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
            proxy.onOpen(session);
//        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        proxy.onClose(session);
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        proxy.onMessage(session, buffer);
    }
}
