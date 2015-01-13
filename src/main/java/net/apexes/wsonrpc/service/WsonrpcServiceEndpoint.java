package net.apexes.wsonrpc.service;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WebSocketWsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public abstract class WsonrpcServiceEndpoint {

    private final WsonrpcDispatcher dispatcher;

    protected WsonrpcServiceEndpoint(WsonrpcConfig config) {
        dispatcher = new WsonrpcDispatcher(config);
    }

    public WsonrpcServiceEndpoint addService(String name, Object handler) {
        dispatcher.addService(name, handler);
        return this;
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    @OnOpen
    public void onOpen(Session session) {
        WsonrpcServiceContext.Remotes.addRemote(new WebSocketWsonrpcSession(session), dispatcher);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        WsonrpcServiceContext.Remotes.removeRemote(session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        try {
            WsonrpcSession wsonrpcSession = WsonrpcServiceContext.Remotes.getSession(session.getId());
            dispatcher.handleMessage(wsonrpcSession, buffer.array());
        } catch (Exception ex) {
            if (dispatcher.getExceptionProcessor() != null) {
                dispatcher.getExceptionProcessor().onError(ex);
            }
        }
    }

}
