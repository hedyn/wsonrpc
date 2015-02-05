package net.apexes.wsonrpc.server;

import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerBase {
    
    protected final WsonrpcServerEndpoint endpoint;
    
    protected WsonrpcServerBase() {
        endpoint = new WsonrpcServerEndpoint();
    }

    protected WsonrpcServerBase(ExecutorService execService) {
        endpoint = new WsonrpcServerEndpoint(execService);
    }
    
    protected WsonrpcServerBase(WsonrpcConfig config) {
        endpoint = new WsonrpcServerEndpoint(config);
    }
    
    public void setExceptionProcessor(ExceptionProcessor processor) {
        endpoint.setExceptionProcessor(processor);
    }
    
    public ExceptionProcessor getExceptionProcessor() {
        return endpoint.getExceptionProcessor();
    }
    
    public void register(String name, Object handler) {
        endpoint.register(name, handler);
    }
    
    public void register(Object handler) {
        endpoint.register(handler);
    }

}
