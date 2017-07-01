/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcClientListener {

    void onOpen(WsonrpcClient client);

    void onClose(WsonrpcClient client, int code, String reason);
    
    void onSentMessage(byte[] bytes);
    
    void onSentPing();
    
}
