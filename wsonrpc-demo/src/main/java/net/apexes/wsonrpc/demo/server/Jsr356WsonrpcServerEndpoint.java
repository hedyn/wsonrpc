package net.apexes.wsonrpc.demo.server;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.core.MaxSessions;

import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import net.apexes.wsonrpc.core.websocket.WebSockets;
import net.apexes.wsonrpc.demo.api.DemoHandler;
import net.apexes.wsonrpc.demo.api.RegisterHandler;
import net.apexes.wsonrpc.demo.server.handler.DemoHandlerImpl;
import net.apexes.wsonrpc.demo.server.handler.RegisterHandlerImpl;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import net.apexes.wsonrpc.server.WsonrpcServerBase;
import net.apexes.wsonrpc.server.WsonrpcServerConfigBuilder;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc")
public class Jsr356WsonrpcServerEndpoint extends WsonrpcServerBase implements WsonrpcErrorProcessor {

    public Jsr356WsonrpcServerEndpoint() {
        super(WsonrpcServerConfigBuilder.create()
                .json(new JacksonImplementor())
//                .binaryWrapper(new net.apexes.wsonrpc.core.GZIPBinaryWrapper())
                .build());
        setErrorProcessor(this);
        getRegistry()
            .register("demo", new DemoHandlerImpl() , DemoHandler.class)
            .register("register", new RegisterHandlerImpl() , RegisterHandler.class);
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
