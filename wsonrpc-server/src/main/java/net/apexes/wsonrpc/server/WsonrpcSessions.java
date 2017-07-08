/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import net.apexes.wsonrpc.core.WebSocketSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class WsonrpcSessions {

    private WsonrpcSessions() {}

    private static final ThreadLocal<WebSocketSession> sessions = new ThreadLocal<>();

    static void begin(WebSocketSession session) {
        sessions.set(session);
    }

    static void end() {
        sessions.remove();
    }

    public static WebSocketSession get() {
        return sessions.get();
    }
}
