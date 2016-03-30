/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support;

import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClientEndpoint;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;

/**
 * 基于Tyrus jdk client {@link org.glassfish.tyrus.container.jdk.client.JdkClientContainer}的连接
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class TyrusWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(WsonrpcClientEndpoint endpoint, URI uri, long timeout) throws Exception {
        ClientManager mgr = ClientManager.createClient(JdkClientContainer.class.getName());
        mgr.connectToServer(new WebSocketEndpointAdapter(endpoint), uri);
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    @ClientEndpoint
    public static class WebSocketEndpointAdapter {
        
        private final WsonrpcClientEndpoint endpoint;
        
        public WebSocketEndpointAdapter(WsonrpcClientEndpoint endpoint) {
            this.endpoint = endpoint;
        }
        
        @OnOpen
        public void onOpen(Session session) {
            endpoint.onOpen(new WebSocketSessionAdapter(session));
        }

        @OnMessage
        public void onMessage(ByteBuffer buffer) {
            endpoint.onMessage(buffer.array());
        }

        @OnError
        public void onError(Throwable error) {
            endpoint.onError(error);
        }

        @OnClose
        public void onClose(CloseReason closeReason) {
            endpoint.onClose(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
        }
        
    }

}
