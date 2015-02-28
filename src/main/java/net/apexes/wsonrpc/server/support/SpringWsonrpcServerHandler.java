package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerBase;

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
public class SpringWsonrpcServerHandler extends WsonrpcServerBase implements WebSocketHandler {
    
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
        endpoint.onOpen(new SpringWebSocketSessionAdapter(session));
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
        endpoint.onMessage(session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        endpoint.onClose(session.getId());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (getExceptionProcessor() != null) {
            getExceptionProcessor().onError(exception);
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
