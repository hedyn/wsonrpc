package net.apexes.wsonrpc.client.support;

import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.client.WebsocketConnector;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.internal.WebSocketSessionAdapter;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

/**
 * 基于Tyrus jdk client {@link org.glassfish.tyrus.container.jdk.client.JdkClientContainer}的连接
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class TyrusWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(WsonrpcClient client, URI uri) throws Exception {
        WebSocketEndpointAdapter endpoint = new WebSocketEndpointAdapter(client);
        ClientManager mgr = ClientManager.createClient(JdkClientContainer.class.getName());
        mgr.connectToServer(endpoint, uri);
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    @ClientEndpoint
    public static class WebSocketEndpointAdapter {
        
        private final WsonrpcClient client;
        
        public WebSocketEndpointAdapter(WsonrpcClient client) {
            this.client = client;
        }
        
        @OnOpen
        public void onOpen(Session session) {
            client.onOpen(new WebSocketSessionAdapter(session));
        }

        @OnMessage
        public void onMessage(ByteBuffer buffer) {
            client.onMessage(buffer.array());
        }

        @OnError
        public void onError(Throwable error) {
            client.onError(error);
        }

        @OnClose
        public void onClose(CloseReason closeReason) {
            client.onClose(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
        }
        
    }

}
