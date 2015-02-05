package net.apexes.wsonrpc.client.support;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

/**
 * 基于 {@link org.java_websocket.client.WebSocketClient}的连接
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketConnector implements WebsocketConnector {
    
    @Override
    public void connectToServer(WsonrpcClient client, URI uri) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClientAdapter clientAdapter = new WebSocketClientAdapter(uri, client, latch);
        /*
         * connectBlocking() 方法返回后才会触发onOpen(ServerHandshake)，
         * 所以要用CountDownLatch阻塞到onOpen(ServerHandshake)时
         */
        clientAdapter.connectBlocking();
        if (client.getTimeout() > 0) {
            latch.await(client.getTimeout(), TimeUnit.MILLISECONDS);
        } else {
            latch.await();
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketClientAdapter extends WebSocketClient implements WsonrpcSession {
        
        private final WsonrpcClient rpcClient;
        private final CountDownLatch latch;
        private String id;
        private boolean opened;
        
        public WebSocketClientAdapter(URI uri, WsonrpcClient rpcClient, CountDownLatch latch) {
            super(uri, new Draft_17());
            this.rpcClient = rpcClient;
            this.latch = latch;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            id = UUID.randomUUID().toString();
            opened = true;
            rpcClient.onOpen(this);
            latch.countDown();
        }

        @Override
        public void onMessage(String message) {
            rpcClient.onMessage(message.getBytes());
        }
        
        @Override
        public void onMessage(ByteBuffer bytes) {
            rpcClient.onMessage(bytes.array());
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            opened = false;
            rpcClient.onClose(code, reason);
        }

        @Override
        public void onError(Exception error) {
            rpcClient.onError(error);
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
        public void sendBinary(byte[] bytes) throws IOException {
            send(bytes);
        }
    }

}
