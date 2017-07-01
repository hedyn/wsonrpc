package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.core.GZIPBinaryWrapper;
import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.core.websocket.WebSockets;
import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.api.RegisterService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;
import net.apexes.wsonrpc.demo.server.service.RegisterServiceImpl;
import net.apexes.wsonrpc.demo.util.SimpleWsonrpcErrorProcessor;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import net.apexes.wsonrpc.server.WsonrpcServerBase;
import org.glassfish.tyrus.core.MaxSessions;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@MaxSessions(10000)
@ServerEndpoint("/wsonrpc")
public class Jsr356DemoWsonrpcServer {

    private final WsonrpcServerBase serverBase;

    public Jsr356DemoWsonrpcServer() {
        serverBase = new WsonrpcServerBase(WsonrpcConfigBuilder.create()
                .json(new JacksonImplementor())
                .binaryWrapper(new GZIPBinaryWrapper())
                .errorProcessor(new SimpleWsonrpcErrorProcessor())
                .build());
        serverBase.getServiceRegistry()
                .register("demo", new DemoServiceImpl() , DemoService.class)
                .register("register", new RegisterServiceImpl() , RegisterService.class);
    }

    @OnOpen
    public void onOpen(Session session) {
        serverBase.onOpen(WebSockets.createSession(session));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        serverBase.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        serverBase.onMessage(session.getId(), buffer);
    }
}
