package net.apexes.wsonrpc.server.support;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;
import net.apexes.wsonrpc.server.WsonrpcServerBase;

/**
 * 基于 JSR 356 WebSocket API 的服务端
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class Jsr356WsonrpcServerEndpoint extends WsonrpcServerBase {

    
    protected Jsr356WsonrpcServerEndpoint() {
    }
    
    protected Jsr356WsonrpcServerEndpoint(ExecutorService execService) {
        super(execService);
    }
    
    protected Jsr356WsonrpcServerEndpoint(WsonrpcConfig config) {
        super(config);
    }

    @OnOpen
    public void onOpen(Session session) {
        endpoint.onOpen(new WebSocketSessionAdapter(session));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        endpoint.onClose(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer buffer) {
        endpoint.onMessage(session.getId(), buffer);
    }

}
