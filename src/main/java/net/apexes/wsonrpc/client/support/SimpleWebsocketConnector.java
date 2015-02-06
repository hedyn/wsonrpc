package net.apexes.wsonrpc.client.support;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.client.support.websocket.WebSocketClient;

/**
 * 基于 {@link net.apexes.wsonrpc.client.support.websocket.WebSocketClient}的连接
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(WsonrpcClient client, URI uri) throws Exception {
        WebSocketClient wsClient = new WebSocketClient(uri);
        wsClient.connect(new WebSocketClientProxy(client, wsClient));
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WebSocketClientProxy implements WsonrpcSession, WebSocketClient.Listener  {
        
        private final WsonrpcClient rpcClient;
        private final WebSocketClient wsClient;
        private String id;
        private volatile boolean opened;
        
        WebSocketClientProxy(WsonrpcClient rpcClient, WebSocketClient wsClient) {
            this.rpcClient = rpcClient;
            this.wsClient = wsClient;
            opened = false;
        }

        @Override
        public void onConnect() {
            id = UUID.randomUUID().toString();
            opened = true;
            rpcClient.onOpen(this);
        }

        @Override
        public void onMessage(String message) {
            onMessage(message.getBytes());
        }

        @Override
        public void onMessage(byte[] data) {
            rpcClient.onMessage(data);
        }

        @Override
        public void onDisconnect(int code, String reason) {
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
        public void sendBinary(byte[] data) throws IOException {
            wsClient.send(data);
        }

        @Override
        public void close() throws IOException {
            wsClient.disconnect();
        }
        
    }

}
