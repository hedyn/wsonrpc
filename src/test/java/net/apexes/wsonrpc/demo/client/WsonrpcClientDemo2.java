package net.apexes.wsonrpc.demo.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.GsonJsonContext;
import net.apexes.jsonrpc.JsonRpcLogger;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.client.WsonrpcClientListener;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.demo.api.User;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcClientDemo2 {
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        WsonrpcClientDemo2 client = new WsonrpcClientDemo2();
        try {
            client.connect();
            while (true) {
                System.out.print(">");
                String command = reader.readLine();
                if (command.isEmpty()) {
                    continue;
                }
                if ("exit".equalsIgnoreCase(command)) {
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
        System.out.println("::closed!");
    }
    
    private final ExecutorService execService;
    private final WsonrpcClient client;
    
    WsonrpcClientDemo2() throws Exception {
        execService = Executors.newCachedThreadPool();
        GsonJsonContext jsonContext = new GsonJsonContext();
        jsonContext.setLogger(new JsonRpcLogger() {

            @Override
            public void onRead(String json) {
                System.err.println("onRead: " + json);
            }

            @Override
            public void onWrite(String json) {
                System.err.println("onWrite: " + json);
            }
        });
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonContext(jsonContext).build(execService);
        URI uri = new URI("ws://127.0.0.1:8080/wsonrpc/0");
        client = WsonrpcClient.Builder.create(uri)
//                .connector(new net.apexes.wsonrpc.client.support.JavaWebsocketConnector())
//                .connector(new net.apexes.wsonrpc.client.support.TyrusWebsocketConnector())
                .build(config);
        
        // 供Server端调用的接口
        client.getServiceRegistry().register(new CallClientServiceImpl());
        client.setClientListener(new WsonrpcClientListener() {
            
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void onOpen(WsonrpcClient client) {
                System.out.println("::onOpen");
                
            }

            @Override
            public void onClose(WsonrpcClient client, int code, String reason) {
                // 1006: Closed abnormally.
                System.out.println("::onClose: " + code + ": " + reason);
            }

            @Override
            public void onSentMessage(byte[] bytes) {
                System.out.println("::onSentMessage: length=" + bytes.length);
            }

            @Override
            public void onSentPing() {
                System.out.println("::onSentPing");
            }
            
        });
    }
    
    public void connect() throws Exception {
        client.connect();
    }
    
    public void close() throws Exception {
        client.close();
        execService.shutdownNow();
    }
    
    public void login(String username, String password) {
        LoginService srv = WsonrpcRemote.Executor.create(client).getService(LoginService.class);
        User user = srv.login(username, password);
        System.out.println("::login(" + username + ", " + password + "): " + user);
    }
    
    public void ping() throws Exception {
        client.ping();
    }

}
