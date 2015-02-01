package net.apexes.wsonrpc.internal;

import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public abstract class Sessions {

    private Sessions() {}

    private static final ThreadLocal<WsonrpcSession> sessions = new ThreadLocal<>();

    static void begin(WsonrpcSession session) {
        sessions.set(session);
    }

    static void end() {
        sessions.remove();
    }

    public static WsonrpcSession get() {
        return sessions.get();
    }
}
