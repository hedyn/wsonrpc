/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.DefaultWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.server.WebSocketServer.WebSocketServerFactory;

import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerEndpoint;

/**
 * 基于 {@link org.java_websocket.server.WebSocketServer}的服务端
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketWsonrpcServer extends WsonrpcServerEndpoint {
    
    protected final WebSocketServer wsServer;
    
    private volatile AtomicBoolean isclosed = new AtomicBoolean(false);
    
    private final WebSocketServerFactory wsf = new DefaultWebSocketServerFactory() {

        @Override
        public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d, Socket s) {
            return new SessionWebSocket(a, d);
        }

        @Override
        public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d, Socket s) {
            return new SessionWebSocket(a, d);
        }
    };

    public JavaWebsocketWsonrpcServer(InetSocketAddress address, PathStrategy pathStrategy) {
        wsServer = new WebSocketServerAdapter(address, pathStrategy, this);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public JavaWebsocketWsonrpcServer(InetSocketAddress address, PathStrategy pathStrategy, ExecutorService execService) {
        super(execService);
        wsServer = new WebSocketServerAdapter(address, pathStrategy, this);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public JavaWebsocketWsonrpcServer(InetSocketAddress address, PathStrategy pathStrategy, WsonrpcConfig config) {
        super(config);
        wsServer = new WebSocketServerAdapter(address, pathStrategy, this);
        wsServer.setWebSocketFactory(wsf);
    }
    
    public void start() {
        isclosed.set(false);
        try {
            wsServer.run();
        } finally {
            isclosed.set(true);
        }
    }
    
    public void stop() throws IOException, InterruptedException {
        isclosed.set(true);
        wsServer.stop();
    }
    
    public boolean isRunning() {
        return !isclosed.get();
    }
    
    private static String toSessionId(WebSocket websocket) {
        if (websocket == null) {
            return null;
        }
        String id;
        if (websocket instanceof SessionWebSocket) {
            id = ((SessionWebSocket) websocket).getId();
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
    public static interface PathStrategy {
        
        boolean accept(String path);
        
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class SessionWebSocket extends WebSocketImpl {
        
        private final String id = UUID.randomUUID().toString();
        
        public SessionWebSocket(WebSocketListener listener , Draft draft) {
            super(listener, draft);
        }
        
        public SessionWebSocket(WebSocketListener listener , List<Draft> drafts) {
            super(listener, drafts);
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
    private static class WebSocketServerAdapter extends WebSocketServer {
        
        private final JavaWebsocketWsonrpcServer server;
        private final PathStrategy pathStrategy;
        
        public WebSocketServerAdapter(InetSocketAddress address, PathStrategy pathStrategy,
                JavaWebsocketWsonrpcServer server) {
            super(address);
            this.pathStrategy = pathStrategy;
            this.server = server;
        }
        
        @Override
        public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket websockt, Draft draft,
                ClientHandshake request) throws InvalidDataException {
            if (pathStrategy != null) {
                if (!pathStrategy.accept(request.getResourceDescriptor())) {
                    throw new InvalidDataException(CloseFrame.TLS_ERROR, request.getResourceDescriptor());
                }
            }
            return super.onWebsocketHandshakeReceivedAsServer(websockt, draft, request);
        }

        @Override
        public void onOpen(WebSocket websockt, ClientHandshake handshake) {
            server.onOpen(new JavaWebSocketSessionAdapter(websockt));
        }

        @Override
        public void onClose(WebSocket websockt, int code, String reason, boolean remote) {
            server.onClose(toSessionId(websockt));
        }

        @Override
        public void onMessage(WebSocket websockt, String message) {
            server.onMessage(toSessionId(websockt), ByteBuffer.wrap(message.getBytes()));
        }
        
        @Override
        public void onMessage(WebSocket websockt, ByteBuffer message ) {
            server.onMessage(toSessionId(websockt), message);
        }
        
        @Override
        public void onError(WebSocket websockt, Exception ex) {
            if (server.isRunning()) {
                server.onError(toSessionId(websockt), ex);
            }
        }
        
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class JavaWebSocketSessionAdapter implements WsonrpcSession {
        
        private static final FramedataImpl1 PING_FRAME = new FramedataImpl1(Opcode.PING);
        static {
            PING_FRAME.setFin(true);
        }
        
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
        public void ping() throws IOException {
            websocket.sendFrame(PING_FRAME);
        }

        @Override
        public void close() throws IOException {
            websocket.close();
        }
        
    }

}
