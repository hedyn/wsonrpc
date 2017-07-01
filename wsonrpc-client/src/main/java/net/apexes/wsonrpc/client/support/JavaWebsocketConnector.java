/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClientEndpoint;
import net.apexes.wsonrpc.core.WsonrpcSession;

/**
 * 基于 {@link org.java_websocket.client.WebSocketClient}的连接
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketConnector implements WebsocketConnector {
    
    @Override
    public void connectToServer(WsonrpcClientEndpoint endpoint, URI uri) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClientAdapter clientAdapter = new WebSocketClientAdapter(uri, endpoint, latch);
        /*
         * connectBlocking()方法返回后才会触发onOpen(ServerHandshake)，
         * 所以要用CountDownLatch阻塞到onOpen(ServerHandshake)时
         */
        clientAdapter.connectBlocking();
        int timeout = endpoint.getConnectTimeout();
        if (timeout > 0) {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } else {
            latch.await();
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketClientAdapter extends WebSocketClient implements WsonrpcSession {
        
        private static FramedataImpl1 PING_FRAME = new FramedataImpl1(Opcode.PING);
        static {
            PING_FRAME.setFin(true);
        }
        
        private final WsonrpcClientEndpoint endpoint;
        private final CountDownLatch latch;
        private String id;
        private volatile boolean opened;
        
        public WebSocketClientAdapter(URI uri, WsonrpcClientEndpoint endpoint, CountDownLatch latch) {
            super(uri, new Draft_17());
            this.endpoint = endpoint;
            this.latch = latch;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            id = UUID.randomUUID().toString();
            opened = true;
            endpoint.onOpen(this);
            latch.countDown();
        }

        @Override
        public void onMessage(String message) {
            try {
                endpoint.onMessage(message.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        
        @Override
        public void onMessage(ByteBuffer bytes) {
            endpoint.onMessage(bytes.array());
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            opened = false;
            endpoint.onClose(code, reason);
        }

        @Override
        public void onError(Exception error) {
            endpoint.onError(error);
        }
        
        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isOpen() {
            return opened;
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            send(bytes);
        }

        @Override
        public void ping() throws IOException {
            getConnection().sendFrame(PING_FRAME);
        }
        
    }

}
