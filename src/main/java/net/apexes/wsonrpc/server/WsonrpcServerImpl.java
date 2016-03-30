/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
class WsonrpcServerImpl {
    
    private final WsonrpcDispatcher dispatcher;
    private WsonrpcServerListener serverListener;
    
    public WsonrpcServerImpl() {
        this(Executors.newCachedThreadPool());
    }
    
    public WsonrpcServerImpl(ExecutorService execService) {
        this(WsonrpcConfig.Builder.create().build(execService));
    }

    public WsonrpcServerImpl(WsonrpcConfig config) {
        if (config == null) {
            config = WsonrpcConfig.Builder.create().build(Executors.newCachedThreadPool());
        }
        dispatcher = new WsonrpcDispatcher(config);
    }
    
    public ServiceRegistry getServiceRegistry() {
        return dispatcher.getServiceRegistry();
    }

    public void setErrorProcessor(ErrorProcessor processor) {
        dispatcher.setErrorProcessor(processor);
    }
    
    public ErrorProcessor getErrorProcessor() {
        return dispatcher.getErrorProcessor();
    }
    
    public void setServerListener(WsonrpcServerListener listener) {
        this.serverListener = listener;
    }

    /**
     * Client端已经连接上
     * @param session
     */
    protected void onOpen(WsonrpcSession session) {
        Remotes.addRemote(session, dispatcher);
        fireOpen(session);
    }

    /**
     * Client端被关闭了
     * @param sessionId
     */
    protected void onClose(String sessionId) {
        Remotes.removeRemote(sessionId);
        fireClose(sessionId);
    }

    /**
     * 收到Client端发来的数据
     * @param sessionId
     * @param buffer
     */
    protected void onMessage(String sessionId, ByteBuffer buffer) {
        WsonrpcSession wsonrpcSession = Remotes.getSession(sessionId);
        byte[] bytes = buffer.array();
        try {
            dispatcher.handleMessage(wsonrpcSession, bytes);
        } catch (Exception ex) {
            onError(sessionId, ex);
        } finally {
            fireMessage(sessionId, bytes);
        }
    }
    
    protected void onError(String sessionId, Throwable error) {
        if (dispatcher.getErrorProcessor() != null) {
            dispatcher.getErrorProcessor().onError(sessionId, error);
        }
    }
    
    private void fireOpen(WsonrpcSession session) {
        if (serverListener != null) {
            serverListener.onOpen(session);
        }
    }

    private void fireClose(String sessionId) {
        if (serverListener != null) {
            serverListener.onClose(sessionId);
        }
    }
    
    private void fireMessage(String sessionId, byte[] bytes) {
        if (serverListener != null) {
            serverListener.onMessage(sessionId, bytes);
        }
    }

}
