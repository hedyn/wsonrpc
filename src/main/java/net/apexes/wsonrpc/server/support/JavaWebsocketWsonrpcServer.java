package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerBase;
import net.apexes.wsonrpc.server.WsonrpcServerEndpoint;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.server.WebSocketServer.WebSocketServerFactory;

/**
 * 基于 {@link org.java_websocket.server.WebSocketServer}的服务端
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketWsonrpcServer extends WsonrpcServerBase {
    
    protected final WebSocketServer wsServer;
    
    private final WebSocketServerFactory wsf = new DefaultWebSocketServerFactory() {

        @Override
        public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d, Socket s) {
            return new SessionWebSocketImpl(a, d);
        }

        @Override
        public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d, Socket s) {
            return new SessionWebSocketImpl(a, d);
        }
    };

    public JavaWebsocketWsonrpcServer(InetSocketAddress address) {
        wsServer = new WebSocketServerProxy(address, endpoint);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public JavaWebsocketWsonrpcServer(InetSocketAddress address, ExecutorService execService) {
        super(execService);
        wsServer = new WebSocketServerProxy(address, endpoint);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public JavaWebsocketWsonrpcServer(InetSocketAddress address, WsonrpcConfig config) {
        super(config);
        wsServer = new WebSocketServerProxy(address, endpoint);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public void run() {
        wsServer.run();
    }
    
    private static String toSessionId(WebSocket websocket) {
        String id;
        if (websocket instanceof SessionWebSocketImpl) {
            id = ((SessionWebSocketImpl) websocket).getId();
        } else {
            id = websocket.getRemoteSocketAddress().toString();
        }
        return id;
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class SessionWebSocketImpl extends WebSocketImpl {
        
        private final String id;
        
        public SessionWebSocketImpl(WebSocketListener listener , Draft draft) {
            super(listener, draft);
            id = UUID.randomUUID().toString();
        }
        
        public SessionWebSocketImpl(WebSocketListener listener , List<Draft> drafts) {
            super(listener, drafts);
            id = UUID.randomUUID().toString();
        }

        String getId() {
            return id;
        }
        
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketServerProxy extends WebSocketServer {
        
        private final WsonrpcServerEndpoint endpoint;

        public WebSocketServerProxy(InetSocketAddress address, WsonrpcServerEndpoint endpoint) {
            super(address);
            this.endpoint = endpoint;
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            endpoint.onOpen(new JavaWebSocketSessionAdapter(conn));
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            endpoint.onClose(toSessionId(conn));
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            endpoint.onMessage(toSessionId(conn), ByteBuffer.wrap(message.getBytes()));
        }
        
        @Override
        public void onMessage( WebSocket conn, ByteBuffer message ) {
            endpoint.onMessage(toSessionId(conn), message);
        }
        
        @Override
        public void onError(WebSocket conn, Exception ex) {
            if (endpoint.getExceptionProcessor() != null) {
                endpoint.getExceptionProcessor().onError(ex);
            }
        }
        
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class JavaWebSocketSessionAdapter implements WsonrpcSession {
        
        private final WebSocket websocket;
        
        JavaWebSocketSessionAdapter(WebSocket websocket) {
            this.websocket = websocket;
        }

        @Override
        public String getId() {
            return toSessionId(websocket);
        }

        @Override
        public boolean isOpen() {
            return websocket.isOpen();
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            websocket.send(bytes);
        }

        @Override
        public void close() throws IOException {
            websocket.close();
        }
        
    }

}
