/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import net.apexes.jsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class WsonrpcServerEndpoint {
    
    private final WsonrpcServerImpl impl;
    
    protected WsonrpcServerEndpoint() {
        impl = new WsonrpcServerImpl();
    }

    protected WsonrpcServerEndpoint(ExecutorService execService) {
        impl = new WsonrpcServerImpl(execService);
    }
    
    protected WsonrpcServerEndpoint(WsonrpcConfig config) {
        impl = new WsonrpcServerImpl(config);
    }
    
    public ServiceRegistry getServiceRegistry() {
        return impl.getServiceRegistry();
    }
    
    public void setErrorProcessor(ErrorProcessor processor) {
        impl.setErrorProcessor(processor);
    }
    
    public ErrorProcessor getErrorProcessor() {
        return impl.getErrorProcessor();
    }
    
    public void setServerListener(WsonrpcServerListener listener) {
        impl.setServerListener(listener);
    }
    
    protected void onError(String sessionId, Throwable error) {
        impl.onError(sessionId, error);
    }
    
    /**
     * Client端已经连接上
     * @param session
     */
    protected void onOpen(WsonrpcSession session) {
        impl.onOpen(session);
    }

    /**
     * Client端被关闭了
     * @param sessionId
     */
    protected void onClose(String sessionId) {
        impl.onClose(sessionId);
    }

    /**
     * 收到Client端发来的数据
     * @param sessionId
     * @param buffer
     */
    protected void onMessage(String sessionId, ByteBuffer buffer) {
        impl.onMessage(sessionId, buffer);
    }

}
