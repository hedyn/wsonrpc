package net.apexes.wsonrpc.internal;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public abstract class WsonrpcServerEndpoint {

    private final WsonrpcDispatcher dispatcher;

    protected WsonrpcServerEndpoint(WsonrpcConfig config) {
        dispatcher = new WsonrpcDispatcher(config);
    }

    public WsonrpcServerEndpoint addService(String name, Object handler) {
        dispatcher.addService(name, handler);
        return this;
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    @OnOpen
    public void onOpen(Session session) {
        WsonrpcContext.Remotes.addRemote(session, dispatcher);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        WsonrpcContext.Remotes.removeRemote(session);
    }

    @OnMessage
    public void handle(final Session session, final ByteBuffer buffer) {
        try {
            dispatcher.handle(session, buffer);
        } catch (Exception ex) {
            if (dispatcher.getExceptionProcessor() != null) {
                dispatcher.getExceptionProcessor().onError(ex);
            }
        }
    }

}
