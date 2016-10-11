/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import net.apexes.wsonrpc.core.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class WsonrpcSessions {

    private WsonrpcSessions() {}

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
