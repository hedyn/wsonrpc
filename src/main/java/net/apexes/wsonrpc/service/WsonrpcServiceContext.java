package net.apexes.wsonrpc.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.ICaller;
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

        private static final Map<String, WsonrpcServiceEndpointProxy> remotes = new ConcurrentHashMap<>();

        static void addRemote(WsonrpcSession session, ICaller caller) {
            remotes.put(session.getId(), new WsonrpcServiceEndpointProxy(session, caller));
        }
        
        static void removeRemote(String sessionId) {
            remotes.remove(sessionId);
        }

        static WsonrpcSession getSession(String sessionId) {
            WsonrpcServiceEndpointProxy endpoint = remotes.get(sessionId);
            if (endpoint == null) {
                return null;
            }
            return endpoint.getSession();
        }
        
        public static WsonrpcRemote getRemote(String sessionId) {
            return remotes.get(sessionId);
        }

        public static Collection<? extends WsonrpcRemote> getRemotes() {
            return remotes.values();
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    static class WsonrpcServiceEndpointProxy extends WsonrpcEndpoint {
        
        WsonrpcServiceEndpointProxy(WsonrpcSession session, ICaller caller) {
            online(session, caller);
        }
        
        @Override
        public WsonrpcSession getSession() {
            return super.getSession();
        }

    }
}
