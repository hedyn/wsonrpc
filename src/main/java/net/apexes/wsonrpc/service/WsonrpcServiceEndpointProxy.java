package net.apexes.wsonrpc.service;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import javax.websocket.Session;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServiceEndpointProxy {
    
    private final WsonrpcDispatcher dispatcher;

    public WsonrpcServiceEndpointProxy(WsonrpcConfig config) {
        dispatcher = new WsonrpcDispatcher(config);
    }
    
    public WsonrpcServiceEndpointProxy(ExecutorService execService) {
        dispatcher = new WsonrpcDispatcher(WsonrpcConfig.Builder.create().build(execService));
    }

    public void addService(String name, Object handler) {
        dispatcher.addService(name, handler);
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    public void onOpen(Session session) {
        WsonrpcServiceContext.Remotes.addRemote(new WebSocketSessionAdapter(session), dispatcher);
    }

    public void onClose(Session session) {
        WsonrpcServiceContext.Remotes.removeRemote(session.getId());
    }

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
