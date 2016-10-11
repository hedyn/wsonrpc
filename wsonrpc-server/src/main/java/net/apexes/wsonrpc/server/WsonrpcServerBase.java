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
import net.apexes.wsonrpc.core.WsonrpcControl;
import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import net.apexes.wsonrpc.core.WsonrpcSession;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerBase implements WsonrpcServer {
    
    protected final WsonrpcConfig config;
    private final WsonrpcControl wsonrpcControl;
    private WsonrpcServerListener serverListener;
    private WsonrpcErrorProcessor errorProcessor;
    
    public WsonrpcServerBase(WsonrpcConfig config) {
        this.config = config;
        wsonrpcControl = new InnerWsonrpcControl(config);
    }

    /**
     * Client端已经连接上
     * @param session
     */
    public void onOpen(WsonrpcSession session) {
        WsonrpcRemotes.addRemote(session, wsonrpcControl.getConfig());
        fireOpen(session);
    }

    /**
     * Client端被关闭了
     * @param sessionId
     */
    public void onClose(String sessionId) {
        WsonrpcRemotes.removeRemote(sessionId);
        fireClose(sessionId);
    }

    /**
     * 收到Client端发来的数据
     * @param sessionId
     * @param buffer
     */
    public void onMessage(String sessionId, ByteBuffer buffer) {
        WsonrpcSession session = WsonrpcRemotes.getSession(sessionId);
        byte[] bytes = buffer.array();
        try {
            wsonrpcControl.handle(session, bytes, errorProcessor);
        } catch (Exception ex) {
            onError(session.getId(), ex);
        } finally {
            fireMessage(session.getId(), bytes);
        }
    }
    
    public void onError(String sessionId, Throwable error) {
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
    
    @Override
    public ServiceRegistry getRegistry() {
        return wsonrpcControl;
    }
    
    @Override
    public WsonrpcServerListener getServerListener() {
        return serverListener;
    }

    @Override
    public void setServerListener(WsonrpcServerListener listener) {
        this.serverListener = listener;
    }
    
    @Override
    public WsonrpcErrorProcessor getErrorProcessor() {
        return errorProcessor;
    }

    @Override
    public void setErrorProcessor(WsonrpcErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private class InnerWsonrpcControl extends WsonrpcControl {
        
        public InnerWsonrpcControl(WsonrpcConfig config) {
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
