/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class Sessions {

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
