/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import net.apexes.wsonrpc.core.HandlerRegistry;
import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import net.apexes.wsonrpc.core.WsonrpcKernel;
import net.apexes.wsonrpc.core.WsonrpcSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerBase {
    
    protected final WsonrpcServerConfig config;
    private final WsonrpcKernel wsonrpcKernal;
    private WsonrpcServerListener serverListener;
    private WsonrpcErrorProcessor errorProcessor;
    
    protected WsonrpcServerBase(WsonrpcServerConfig config) {
        this.config = config;
        wsonrpcKernal = new WsonrpcKernel(config);
    }
    
    public HandlerRegistry getRegistry() {
        return wsonrpcKernal;
    }

    public WsonrpcErrorProcessor getErrorProcessor() {
        return errorProcessor;
    }

    public void setErrorProcessor(WsonrpcErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }
    
    public WsonrpcServerListener getServerListener() {
        return serverListener;
    }

    public void setServerListener(WsonrpcServerListener listener) {
        this.serverListener = listener;
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
        config.getExecutor().execute(new ExecCallback(session, buffer.array()));
    }
    
    protected void onError(String sessionId, Throwable error) {
        if (getErrorProcessor() != null) {
            getErrorProcessor().onError(sessionId, error);
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
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    private class ExecCallback implements Runnable {
        
        private final WsonrpcSession session;
        private final byte[] bytes;
        
        private ExecCallback(WsonrpcSession session, byte[] bytes) {
            this.session = session;
            this.bytes = bytes;
        }
        
        @Override
        public void run() {
            WsonrpcSessions.begin(session);
            try {
                wsonrpcKernal.handle(session, new ByteArrayInputStream(bytes));
            } catch (Exception ex) {
                onError(session.getId(), ex);
            } finally {
                fireMessage(session.getId(), bytes);
                WsonrpcSessions.end();
            }
        }
        
    }

}
