package net.apexes.wsonrpc.demo.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.GsonJsonContext;
import net.apexes.jsonrpc.JsonRpcLogger;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.demo.api.CallClientService;
import net.apexes.wsonrpc.server.Remotes;
import net.apexes.wsonrpc.server.WsonrpcServerListener;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer.PathStrategy;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketServerDemo {
    
    public static void main(String[] args) throws Exception {
        JavaWebsocketWsonrpcServer server = createServer();
        try {
            
        } finally {
            stop(server);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(">");
            String command = reader.readLine();
            if (command.isEmpty()) {
                continue;
            }
            if ("start".equalsIgnoreCase(command)) {
                start(server);
            } else if ("stop".equalsIgnoreCase(command)) {
                stop(server);
            } else if ("exit".equalsIgnoreCase(command)) {
                break;
            } else if ("call".equalsIgnoreCase(command)) {
                call();
            } else if ("ping".equalsIgnoreCase(command)) {
                ping();
            }
        }
    }
    
    private static ExecutorService execService = Executors.newCachedThreadPool();
    
    static void start(final JavaWebsocketWsonrpcServer server) {
        execService.execute(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        });
    }
    
    static void stop(JavaWebsocketWsonrpcServer server) {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        execService.shutdownNow();
    }
    
    static void call() {
        for (WsonrpcRemote remote : Remotes.getRemotes()) {
            if (remote != null) {
                CallClientService callClientSrv = WsonrpcRemote.Executor.create(remote)
                        .getService(CallClientService.class);
                String result = callClientSrv.callClient("server");
                System.out.println("result: " + result);
            }
        }
    }
    
    static void ping() {
        for (WsonrpcRemote remote : Remotes.getRemotes()) {
            if (remote != null) {
                try {
                    remote.ping();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static JavaWebsocketWsonrpcServer createServer() {
        InetSocketAddress address = new InetSocketAddress(8080);
        GsonJsonContext jsonContext = new GsonJsonContext();
        jsonContext.setLogger(new JsonRpcLogger() {

            @Override
            public void onRead(String json) {
//                System.err.println("onRead: " + json);
            }

            @Override
            public void onWrite(String json) {
//                System.err.println("onWrite: " + json);
            }
        });
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonContext(jsonContext).build(execService);
        PathStrategy pathStrategy = new PathStrategy() {

            @Override
            public boolean accept(String path) {
                return path.startsWith("/wsonrpc/");
            }
            
        };
        JavaWebsocketWsonrpcServer server = new JavaWebsocketWsonrpcServer(address, pathStrategy, config);
        server.setErrorProcessor(new ErrorProcessor() {

            @Override
            public void onError(String sessionId, Throwable error) {
                error.printStackTrace();
            }
        });
        server.setServerListener(new WsonrpcServerListener() {

            @Override
            public void onOpen(WsonrpcSession session) {
                System.out.println("::onOpen: " + session.getId());
            }

            @Override
            public void onClose(String sessionId) {
                System.out.println("::onClose: " + sessionId);
            }

            @Override
            public void onMessage(String sessionId, byte[] bytes) {
//                System.out.println("::onMessage: " + sessionId + ", length=" + bytes.length);
            }
        });
        server.getServiceRegistry().register(new LoginServiceImpl());
        server.getServiceRegistry().register(new RegisterServiceImpl());
        
        return server;
    }

}
