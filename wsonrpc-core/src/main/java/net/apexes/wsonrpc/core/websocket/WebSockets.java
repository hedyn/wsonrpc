/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core.websocket;

import javax.websocket.Session;

import net.apexes.wsonrpc.core.WebSocketSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class WebSockets {
    private WebSockets() {}
    
    /**
     * 
     * @param session
     * @return
     */
    public static WebSocketSession createSession(Session session) {
        return new WebSocketSessionAdapter(session);
    }
}
