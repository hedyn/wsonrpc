package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public abstract class WsonrpcServerEndpoint {

    protected final WsonrpcServerProxy proxy;

    protected WsonrpcServerEndpoint(WsonrpcConfig config) {
        proxy = new WsonrpcServerProxy(config);
    }

    public void register(String name, Object handler) {
        proxy.register(name, handler);
    }
    
    public void register(Object handler) {
        proxy.register(handler);
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        proxy.setExceptionProcessor(processor);
    }

    @OnOpen
    public void onOpen(Session session) {
        proxy.onOpen(new WebSocketSessionAdapter(session));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        proxy.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer buffer) {
        proxy.onMessage(session.getId(), buffer);
    }

}
