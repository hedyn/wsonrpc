package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerProxy;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SpringWebSocketHandler extends BinaryWebSocketHandler {
    
    private final WsonrpcServerProxy proxy;
    
    public SpringWebSocketHandler() {
        proxy = new WsonrpcServerProxy(Executors.newCachedThreadPool());
    }
    
    public SpringWebSocketHandler(WsonrpcConfig config) {
        proxy = new WsonrpcServerProxy(config);
    }

    public SpringWebSocketHandler(ExecutorService execService) {
        proxy = new WsonrpcServerProxy(execService);
    }
    
    protected void setExceptionProcessor(ExceptionProcessor processor) {
        proxy.setExceptionProcessor(processor);
    }
    
    protected ExceptionProcessor getExceptionProcessor() {
        return proxy.getExceptionProcessor();
    }
    
    protected void register(String name, Object handler) {
        proxy.register(name, handler);
    }
    
    protected void register(Object handler) {
        proxy.register(handler);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        proxy.onOpen(new SpringWebSocketSessionAdapter(session));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        proxy.onMessage(session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        proxy.onClose(session.getId());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (getExceptionProcessor() != null) {
            getExceptionProcessor().onError(exception);
        }
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
        public void close() throws IOException {
            session.close();
        }
        
    }

}
