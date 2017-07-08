/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support;

import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClientEndpoint;
import net.apexes.wsonrpc.client.support.websocket.WebSocketClient;
import net.apexes.wsonrpc.client.support.websocket.WebSocketEventHandler;
import net.apexes.wsonrpc.client.support.websocket.WebSocketException;
import net.apexes.wsonrpc.client.support.websocket.WebSocketMessage;
import net.apexes.wsonrpc.core.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * 基于 {@link WebSocketClient}的连接
 *
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(WsonrpcClientEndpoint endpoint, URI uri) throws Exception {
        WebSocketClient wsClient = new WebSocketClient(uri);
        wsClient.setEventHandler(new WebSocketClientProxy(endpoint, wsClient));
        wsClient.connect();
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketClientProxy implements WebSocketSession, WebSocketEventHandler {
        
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
        public void onOpen() {
            id = UUID.randomUUID().toString();
            opened = true;
            endpoint.onOpen(this);
        }
    
        @Override
        public void onClose() {
            if (opened) {
                opened = false;
                wsClient.close();
                endpoint.onClose(0, "");
            }
        }
    
        @Override
        public void onMessage(WebSocketMessage message) {
            endpoint.onMessage(message.getBytes());
        }
    
        @Override
        public void onError(WebSocketException error) {
            endpoint.onError(error);
        }
    
        @Override
        public void onLogMessage(String msg) {
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
            wsClient.ping();
        }

        @Override
        public void close() throws IOException {
            wsClient.close();
        }
    }
}
