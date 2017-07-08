package net.apexes.wsonrpc.server.support;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.impl.FrameType;
import io.vertx.core.http.impl.ws.WebSocketFrameImpl;
import net.apexes.wsonrpc.core.WebSocketSession;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.server.WsonrpcServer;
import net.apexes.wsonrpc.server.WsonrpcServerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class VertxWsonrpcHandler implements Handler<ServerWebSocket> {
    
    private static final Logger LOG = LoggerFactory.getLogger(VertxWsonrpcHandler.class);
    
    protected final WsonrpcServerBase serverBase;
    
    public VertxWsonrpcHandler(WsonrpcConfig config) {
        serverBase = new WsonrpcServerBase(config);
    }
    
    @Override
    public void handle(ServerWebSocket webSocket) {
        LOG.debug("connect... {}", sessionId(webSocket));
        serverBase.onOpen(new ServerWebSocketSessionAdapter(webSocket));
        webSocket.closeHandler(v -> serverBase.onClose(sessionId(webSocket)));
        webSocket.frameHandler(frame -> serverBase.onMessage(sessionId(webSocket),
                ByteBuffer.wrap(frame.binaryData().getBytes())));
    }
    
    public WsonrpcServer getWsonrpcServer() {
        return serverBase;
    }
    
    private static String sessionId(ServerWebSocket webSocket) {
        return webSocket.binaryHandlerID();
    }
    
    private static WebSocketFrameImpl PING_FRAME = new WebSocketFrameImpl(FrameType.PING);
    
    private static class ServerWebSocketSessionAdapter implements WebSocketSession {
        
        private final ServerWebSocket webSocket;
        private boolean close;
    
        ServerWebSocketSessionAdapter(ServerWebSocket webSocket) {
            this.webSocket = webSocket;
            this.close = false;
        }
    
        @Override
        public String getId() {
            return sessionId(webSocket);
        }
    
        @Override
        public boolean isOpen() {
            return !close;
        }
    
        @Override
        public void ping() throws IOException {
            webSocket.writeFrame(PING_FRAME);
        }
    
        @Override
        public void close() throws IOException {
            close = true;
            webSocket.end();
        }
    
        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            webSocket.write(Buffer.buffer(bytes));
        }
    }
}
