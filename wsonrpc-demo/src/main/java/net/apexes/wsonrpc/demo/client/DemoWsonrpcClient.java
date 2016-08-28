package net.apexes.wsonrpc.demo.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.client.Wsonrpc;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.client.WsonrpcClientListener;
import net.apexes.wsonrpc.demo.api.PushHandler;
import net.apexes.wsonrpc.demo.api.RegisterHandler;
import net.apexes.wsonrpc.demo.api.model.User;
import net.apexes.wsonrpc.demo.client.handler.PushHandlerImpl;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoWsonrpcClient {
    
    private static final Logger LOG = LoggerFactory.getLogger(DemoWsonrpcClient.class);
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DemoWsonrpcClient client = new DemoWsonrpcClient();
        try {
            while (true) {
                System.out.print(">");
                String command = reader.readLine();
                if (command.isEmpty()) {
                    continue;
                }
                if (command.startsWith("connect ")) {
                    String clientId = command.substring("connect ".length());
                    if (clientId != null && clientId.length() > 0) {
                        client.connect(clientId);
                    }
                } else if ("disconnect".equalsIgnoreCase(command)) {
                    client.disconnect();
                } else if ("exit".equalsIgnoreCase(command)) {
                    client.close();
                    break;
                } else if ("login".equalsIgnoreCase(command)) {
                    client.login("admin", "admin123");
                } else if ("ping".equalsIgnoreCase(command)) {
                    client.ping();
                }
            }
        } finally {
            reader.close();
            client.close();
        }
        LOG.info("closed");
    }
    
    private final WsonrpcClient client;
    private String clientId;
    
    DemoWsonrpcClient() throws Exception {
        String url = "ws://127.0.0.1:8080/wsonrpc";
        client = Wsonrpc.client(url)
//                .connector(new net.apexes.wsonrpc.client.support.JavaWebsocketConnector())
//                .connector(new net.apexes.wsonrpc.client.support.TyrusWebsocketConnector())
                .build();
        
        client.setClientListener(new WsonrpcClientListener() {
            
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void onOpen(WsonrpcClient client) {
                LOG.info("...");
                RegisterHandler handler = Wsonrpc.wsonrpc(client).handleName("register").get(RegisterHandler.class);
                handler.register(clientId);
            }

            @Override
            public void onClose(WsonrpcClient client, int code, String reason) {
                // 1006: Closed abnormally.
                LOG.warn("code={}, reason={}", code, reason);
            }

            @Override
            public void onSentMessage(byte[] bytes) {
                LOG.info("length={}", bytes.length);
            }

            @Override
            public void onSentPing() {
                LOG.info("...");
            }
            
        });
    
        client.getRegistry().register("push", new PushHandlerImpl(), PushHandler.class);
    }
    
    public boolean isConnected() {
        return client.isConnected();
    }
    
    public void connect(String clientId) throws Exception {
        if (!client.isConnected()) {
            this.clientId = clientId;
            client.connect();
        }
    }
    
    public void disconnect() throws Exception {
        if (client.isConnected()) {
            client.disconnect();
        }
    }
    
    public void close() throws Exception {
        if (client.isConnected()) {
            client.disconnect();
        }
    }
    
    public void login(String username, String password) {
        if (client.isConnected()) {
            RegisterHandler handler = Wsonrpc.wsonrpc(client).handleName("register").get(RegisterHandler.class);
            User user = handler.login(username, password);
            LOG.info("{}", user);
        }
    }
    
    public void ping() throws Exception {
        if (client.isConnected()) {
            client.ping();
        }
    }

}
