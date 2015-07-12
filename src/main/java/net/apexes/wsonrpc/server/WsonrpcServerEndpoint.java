package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcServerEndpoint {
    
    private final WsonrpcDispatcher dispatcher;
    
    public WsonrpcServerEndpoint() {
        this(Executors.newCachedThreadPool());
    }
    
    public WsonrpcServerEndpoint(ExecutorService execService) {
        this(WsonrpcConfig.Builder.create().build(execService));
    }

    public WsonrpcServerEndpoint(WsonrpcConfig config) {
        if (config == null) {
            config = WsonrpcConfig.Builder.create().build(Executors.newCachedThreadPool());
        }
        dispatcher = new WsonrpcDispatcher(config);
    }
    
    public ServiceRegistry getServiceRegistry() {
        return dispatcher.getServiceRegistry();
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

}
