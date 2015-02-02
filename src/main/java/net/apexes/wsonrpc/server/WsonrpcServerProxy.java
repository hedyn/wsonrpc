package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.AbstractServiceRegistry;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerProxy extends AbstractServiceRegistry implements ServiceRegistry {
    
    private final WsonrpcDispatcher dispatcher;

    public WsonrpcServerProxy(WsonrpcConfig config) {
        dispatcher = new WsonrpcDispatcher(config);
    }
    
    public WsonrpcServerProxy(ExecutorService execService) {
        dispatcher = new WsonrpcDispatcher(WsonrpcConfig.Builder.create().build(execService));
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }
    
    public ExceptionProcessor getExceptionProcessor() {
        return dispatcher.getExceptionProcessor();
    }

    public void onOpen(WsonrpcSession session) {
        Remotes.addRemote(session, dispatcher);
    }

    public void onClose(String sessionId) {
        Remotes.removeRemote(sessionId);
    }

    public void onMessage(String sessionId, ByteBuffer buffer) {
        try {
            WsonrpcSession wsonrpcSession = Remotes.getSession(sessionId);
            dispatcher.handleMessage(wsonrpcSession, buffer.array());
        } catch (Exception ex) {
            if (dispatcher.getExceptionProcessor() != null) {
                dispatcher.getExceptionProcessor().onError(ex);
            }
        }
    }

    @Override
    public void register(String name, Object handler) {
        dispatcher.addService(name, handler);
    }

}
