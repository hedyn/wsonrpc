/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import net.apexes.wsonrpc.core.WsonrpcSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface WsonrpcClientEndpoint {
    
    int getConnectTimeout();

    void onOpen(WsonrpcSession session);

    void onMessage(byte[] data);

    void onError(Throwable error);

    void onClose(int code, String reason);

}
