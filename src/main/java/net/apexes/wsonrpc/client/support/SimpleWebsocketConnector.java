/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClientEndpoint;
import net.apexes.wsonrpc.client.support.websocket.WebSocketClient;

/**
 * 基于 {@link net.apexes.wsonrpc.client.support.websocket.WebSocketClient}的连接
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(WsonrpcClientEndpoint endpoint, URI uri, long timeout) throws Exception {
        WebSocketClient wsClient = new WebSocketClient(uri);
        wsClient.connect(new WebSocketClientProxy(endpoint, wsClient));
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketClientProxy implements WsonrpcSession, WebSocketClient.Listener  {
        
        private final WsonrpcClientEndpoint endpoint;
        private final WebSocketClient wsClient;
        private String id;
        private volatile boolean opened;
        
        WebSocketClientProxy(WsonrpcClientEndpoint endpoint, WebSocketClient wsClient) {
            this.endpoint = endpoint;
            this.wsClient = wsClient;
            opened = false;
        }

        @Override
        public void onConnect() {
            id = UUID.randomUUID().toString();
            opened = true;
            endpoint.onOpen(this);
        }

        @Override
        public void onMessage(String message) {
            onMessage(message.getBytes());
        }

        @Override
        public void onMessage(byte[] data) {
            endpoint.onMessage(data);
        }

        @Override
        public void onDisconnect(int code, String reason) {
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
        public void sendBinary(byte[] data) throws IOException {
            wsClient.send(data);
        }

        @Override
        public void ping() throws IOException {
            wsClient.sendPing("");
        }

        @Override
        public void close() throws IOException {
            wsClient.disconnect();
        }
        
    }

}
