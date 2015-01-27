package net.apexes.wsonrpc.service;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServiceProxy {
    
    private final WsonrpcDispatcher dispatcher;

    public WsonrpcServiceProxy(WsonrpcConfig config) {
        dispatcher = new WsonrpcDispatcher(config);
    }
    
    public WsonrpcServiceProxy(ExecutorService execService) {
        dispatcher = new WsonrpcDispatcher(WsonrpcConfig.Builder.create().build(execService));
    }

    public void addService(String name, Object handler) {
        dispatcher.addService(name, handler);
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    public void onOpen(WsonrpcSession session) {
        WsonrpcServiceContext.Remotes.addRemote(session, dispatcher);
    }

    public void onClose(String sessionId) {
        WsonrpcServiceContext.Remotes.removeRemote(sessionId);
    }

    public void onMessage(String sessionId, ByteBuffer buffer) {
        try {
            WsonrpcSession wsonrpcSession = WsonrpcServiceContext.Remotes.getSession(sessionId);
            dispatcher.handleMessage(wsonrpcSession, buffer.array());
        } catch (Exception ex) {
            if (dispatcher.getExceptionProcessor() != null) {
                dispatcher.getExceptionProcessor().onError(ex);
            }
        }
    }

}
