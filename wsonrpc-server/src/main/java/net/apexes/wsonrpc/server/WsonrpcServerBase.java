/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.nio.ByteBuffer;

import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import net.apexes.wsonrpc.core.WsonrpcKernel;
import net.apexes.wsonrpc.core.WsonrpcSession;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerBase {
    
    protected final WsonrpcConfig config;
    private final WsonrpcKernel wsonrpcKernal;
    private WsonrpcServerListener serverListener;
    private WsonrpcErrorProcessor errorProcessor;
    
    protected WsonrpcServerBase(WsonrpcConfig config) {
        this.config = config;
        wsonrpcKernal = new WsonrpcKernelProxy(config);
    }
    
    public ServiceRegistry getRegistry() {
        return wsonrpcKernal;
    }
    
    public WsonrpcServerListener getServerListener() {
        return serverListener;
    }

    public void setServerListener(WsonrpcServerListener listener) {
        this.serverListener = listener;
    }
    
    public WsonrpcErrorProcessor getErrorProcessor() {
        return errorProcessor;
    }

    public void setErrorProcessor(WsonrpcErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }

    /**
     * Client端已经连接上
     * @param session
     */
    protected void onOpen(WsonrpcSession session) {
        WsonrpcRemotes.addRemote(session, wsonrpcKernal.getConfig());
        fireOpen(session);
    }

    /**
     * Client端被关闭了
     * @param sessionId
     */
    protected void onClose(String sessionId) {
        WsonrpcRemotes.removeRemote(sessionId);
        fireClose(sessionId);
    }

    /**
     * 收到Client端发来的数据
     * @param sessionId
     * @param buffer
     */
    protected void onMessage(String sessionId, ByteBuffer buffer) {
        WsonrpcSession session = WsonrpcRemotes.getSession(sessionId);
        byte[] bytes = buffer.array();
        try {
            wsonrpcKernal.handle(session, bytes, errorProcessor);
        } catch (Exception ex) {
            onError(session.getId(), ex);
        } finally {
            fireMessage(session.getId(), bytes);
        }
    }
    
    protected void onError(String sessionId, Throwable error) {
        if (errorProcessor != null) {
            errorProcessor.onError(sessionId, error);
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
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private class WsonrpcKernelProxy extends WsonrpcKernel {
        
        public WsonrpcKernelProxy(WsonrpcConfig config) {
            super(config);
        }
        
        @Override
        protected void handleRequest(WsonrpcSession session, JsonRpcRequest request) {
            WsonrpcSessions.begin(session);
            try {
                super.handleRequest(session, request);
            } catch (Exception ex) {
                onError(session.getId(), ex);
            } finally {
                WsonrpcSessions.end();
            }
        }
        
    }
}
