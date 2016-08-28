/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcEndpoint;
import net.apexes.wsonrpc.core.WsonrpcRemote;
import net.apexes.wsonrpc.core.WsonrpcSession;


/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class WsonrpcRemotes {
    
    private WsonrpcRemotes() {}

    private static final Map<String, WsonrpcServerEndpointProxy> remotes = new ConcurrentHashMap<>();

    static void addRemote(WsonrpcSession session, WsonrpcConfig config) {
        remotes.put(session.getId(), new WsonrpcServerEndpointProxy(session, config));
    }
    
    static void removeRemote(String sessionId) {
        remotes.remove(sessionId);
    }

    static WsonrpcSession getSession(String sessionId) {
        WsonrpcServerEndpointProxy endpoint = remotes.get(sessionId);
        if (endpoint == null) {
            return null;
        }
        return endpoint.getSession();
    }
    
    /**
     * 返回指定ID的客户端连接
     * 
     * @param sessionId
     * @return
     */
    public static WsonrpcRemote getRemote(String sessionId) {
        return remotes.get(sessionId);
    }

    /**
     * 返回所有客户端连接
     * 
     * @return
     */
    public static Collection<? extends WsonrpcRemote> getRemotes() {
        return remotes.values();
    }
    
    /**
     * 返回当前线程的客户端连接
     * 
     * @return
     */
    public static WsonrpcRemote getRemote() {
        WsonrpcSession session = WsonrpcSessions.get();
        if (session != null) {
            return getRemote(session.getId());
        }
        return null;
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WsonrpcServerEndpointProxy extends WsonrpcEndpoint {
        
        WsonrpcServerEndpointProxy(WsonrpcSession session, WsonrpcConfig config) {
            super(config);
            online(session);
        }
        
        @Override
        public WsonrpcSession getSession() {
            return super.getSession();
        }

    }
}
