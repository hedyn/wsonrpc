/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerEndpoint;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 基于Spring WebSocket 的服务端
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SpringWsonrpcServerHandler extends WsonrpcServerEndpoint implements WebSocketHandler {
    
    public SpringWsonrpcServerHandler() {
    }

    public SpringWsonrpcServerHandler(ExecutorService execService) {
        super(execService);
    }
    
    public SpringWsonrpcServerHandler(WsonrpcConfig config) {
        super(config);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.onOpen(new SpringWebSocketSessionAdapter(session));
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof BinaryMessage) {
            handleBinaryMessage(session, (BinaryMessage) message);
        } else if (message instanceof PongMessage) {
        } else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.onMessage(session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.onClose(session.getId());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session == null) {
            onError(null, exception);
        } else{
            onError(session.getId(), exception);
        }
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class SpringWebSocketSessionAdapter implements WsonrpcSession {
        
        private final WebSocketSession session;
        
        SpringWebSocketSessionAdapter(WebSocketSession session) {
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
            session.sendMessage(new BinaryMessage(bytes));
        }

        @Override
        public void ping() throws IOException {
            session.sendMessage(new PingMessage());
        }

        @Override
        public void close() throws IOException {
            session.close();
        }
        
    }

}
