/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import net.apexes.wsonrpc.core.WebSocketSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface WsonrpcClientEndpoint {
    
    int getConnectTimeout();

    void onOpen(WebSocketSession session);

    void onMessage(byte[] bytes);

    void onError(Throwable error);

    void onClose(int code, String reason);

}
