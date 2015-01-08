/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.internal.Caller;
import net.apexes.wsonrpc.internal.WsonrpcContext;
import net.apexes.wsonrpc.internal.WsonrpcEndpoint;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcServiceContext extends WsonrpcContext {

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    public static class Remotes {

        private static final Map<String, WsonrpcRemote> remotes = new ConcurrentHashMap<>();

        static void addRemote(Session session, Caller caller) {
            remotes.put(session.getId(), new WsonrpcServiceEndpointProxy(session, caller));
        }

        static void removeRemote(Session session) {
            remotes.remove(session.getId());
        }

        public static WsonrpcRemote getRemote(String sessionId) {
            return remotes.get(sessionId);
        }

        public static Collection<WsonrpcRemote> getRemotes() {
            return remotes.values();
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    static class WsonrpcServiceEndpointProxy extends WsonrpcEndpoint {
        
        WsonrpcServiceEndpointProxy(Session session, Caller caller) {
            online(session, caller);
        }

    }
}
