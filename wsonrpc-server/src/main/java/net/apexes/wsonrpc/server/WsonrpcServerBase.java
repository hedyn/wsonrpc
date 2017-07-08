/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcControl;
import net.apexes.wsonrpc.core.WebSocketSession;

import java.nio.ByteBuffer;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerBase implements WsonrpcServer {
    
    protected final WsonrpcConfig config;
    private final WsonrpcControl wsonrpcControl;
    private WsonrpcServerListener serverListener;

    public WsonrpcServerBase(WsonrpcConfig config) {
        this.config = config;
        wsonrpcControl = new WsonrpcControl(config);
    }

    /**
     * Client端已经连接上
     * @param session
     */
    public void onOpen(WebSocketSession session) {
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
        WebSocketSession session = WsonrpcRemotes.getSession(sessionId);
        byte[] bytes = buffer.array();
        WsonrpcSessions.begin(session);
        try {
            fireMessage(sessionId, bytes);
            wsonrpcControl.handle(session, bytes);
        } catch (Exception ex) {
            onError(session.getId(), ex);
        } finally {
            WsonrpcSessions.end();
        }
    }
    
    public void onError(String sessionId, Throwable error) {
        if (config.getErrorProcessor() != null) {
            config.getErrorProcessor().onError(sessionId, error);
        }
    }
    
    private void fireOpen(WebSocketSession session) {
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
    public ServiceRegistry getServiceRegistry() {
        return wsonrpcControl.getServiceRegistry();
    }
    
    @Override
    public WsonrpcServerListener getServerListener() {
        return serverListener;
    }

    @Override
    public void setServerListener(WsonrpcServerListener listener) {
        this.serverListener = listener;
    }

}
