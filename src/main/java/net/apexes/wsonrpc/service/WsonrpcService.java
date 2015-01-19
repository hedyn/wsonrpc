package net.apexes.wsonrpc.service;

import java.util.Collection;

import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcService {

    /**
     * 返回当前线程的Session
     * 
     * @return
     */
    WsonrpcSession getSession();

    /**
     * 返回所有客户端连接
     * 
     * @return
     */
    Collection<? extends WsonrpcRemote> getRemotes();

    /**
     * 返回指定ID的客户端连接
     * 
     * @param sessionId
     * @return
     */
    WsonrpcRemote getRemote(String sessionId);

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    final class Manager {

        private static WsonrpcService instance = new WsonrpcService() {

            @Override
            public WsonrpcSession getSession() {
                return WsonrpcServiceContext.Sessions.get();
            }

            @Override
            public Collection<? extends WsonrpcRemote> getRemotes() {
                return WsonrpcServiceContext.Remotes.getRemotes();
            }

            @Override
            public WsonrpcRemote getRemote(String sessionId) {
                return WsonrpcServiceContext.Remotes.getRemote(sessionId);
            }

        };
        
        public static WsonrpcSession getSession() {
            return instance.getSession();
        }

        public static WsonrpcRemote getRemote(String sessionId) {
            return instance.getRemote(sessionId);
        }

        public static Collection<? extends WsonrpcRemote> getRemotes() {
            return instance.getRemotes();
        }

        public static WsonrpcRemote getRemote() {
            WsonrpcSession session = instance.getSession();
            if (session != null) {
                return instance.getRemote(session.getId());
            }
            return null;
        }
        
        private Manager() {}

    }

}
