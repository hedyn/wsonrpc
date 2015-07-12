package net.apexes.wsonrpc.server;

import java.util.concurrent.ExecutorService;

import net.apexes.jsonrpc.ServiceRegistry;
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
    
    public ServiceRegistry getServiceRegistry() {
        return endpoint.getServiceRegistry();
    }
    
    public void setExceptionProcessor(ExceptionProcessor processor) {
        endpoint.setExceptionProcessor(processor);
    }
    
    public ExceptionProcessor getExceptionProcessor() {
        return endpoint.getExceptionProcessor();
    }

}
