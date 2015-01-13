package net.apexes.wsonrpc.internal;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.Session;

import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WebSocketWsonrpcSession implements WsonrpcSession {
    
    private final Session session;
    
    public WebSocketWsonrpcSession(Session session) {
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
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

}
