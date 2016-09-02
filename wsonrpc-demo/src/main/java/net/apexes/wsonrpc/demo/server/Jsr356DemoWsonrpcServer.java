package net.apexes.wsonrpc.demo.server;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.core.MaxSessions;

import net.apexes.wsonrpc.core.GZIPBinaryWrapper;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import net.apexes.wsonrpc.core.websocket.WebSockets;
import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.api.RegisterService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;
import net.apexes.wsonrpc.demo.server.service.RegisterServiceImpl;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import net.apexes.wsonrpc.server.WsonrpcServerBase;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc")
public class Jsr356DemoWsonrpcServer extends WsonrpcServerBase implements WsonrpcErrorProcessor {

    public Jsr356DemoWsonrpcServer() {
        super(WsonrpcConfigBuilder.create()
                .json(new JacksonImplementor())
                .binaryWrapper(new GZIPBinaryWrapper())
                .build());
        setErrorProcessor(this);
        getRegistry()
            .register("demo", new DemoServiceImpl() , DemoService.class)
            .register("register", new RegisterServiceImpl() , RegisterService.class);
    }

    @Override
    public void onError(String sessionId, Throwable error) {
        error.printStackTrace();
    }
    
    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(WebSockets.createSession(session));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        super.onMessage(session.getId(), buffer);
    }
}
