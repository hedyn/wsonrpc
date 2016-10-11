/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.Session;

import net.apexes.wsonrpc.core.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
class WebSocketSessionAdapter implements WsonrpcSession {
    
    private static final byte[] EMPTY_ARRAY = {};
    
    private final Session session;
    
    WebSocketSessionAdapter(Session session) {
        this.session = session;
    }
    
    @Override
    public String getId() {
        return session.getId();
    }
    
    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public void sendBinary(byte[] bytes) throws IOException {
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
    }

    @Override
    public void ping() throws IOException {
        session.getBasicRemote().sendPing(ByteBuffer.wrap(EMPTY_ARRAY));
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

}
