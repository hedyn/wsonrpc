/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import net.apexes.wsonrpc.core.WebSocketSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface WsonrpcServerListener {
    
    void onOpen(WebSocketSession session);
    
    void onClose(String sessionId);
    
    void onMessage(String sessionId, byte[] bytes);
    
}
