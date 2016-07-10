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
import net.apexes.wsonrpc.demo.api.CallClientService;
import net.apexes.wsonrpc.demo.api.CallPosService;
import net.apexes.wsonrpc.demo.api.User;
import net.apexes.wsonrpc.server.Remotes;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer.PathStrategy;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerDemo {
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(">");
            String command = reader.readLine();
            if (command.isEmpty()) {
                continue;
            }
            if ("startup".equalsIgnoreCase(command)) {
                start();
            } else if ("shutdown".equalsIgnoreCase(command)) {
                stop();
                System.out.println("Server is shutdown");
                System.exit(0);
                break;
            } else if ("call".equalsIgnoreCase(command)) {
                call();
            } else if ("ping".equalsIgnoreCase(command)) {
                ping();
            }
        }
    }
    
    private static ExecutorService execService;
    private static JavaWebsocketWsonrpcServer server;

    static void start() {
        InetSocketAddress address = new InetSocketAddress(8080);
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
        execService = Executors.newCachedThreadPool();
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonContext(jsonContext).build(execService);
        PathStrategy pathStrategy = new PathStrategy() {

            @Override
            public boolean accept(String path) {
                return path.startsWith("/wsonrpc/");
            }
            
        };
        server = new JavaWebsocketWsonrpcServer(address, pathStrategy, config);
        server.setErrorProcessor(new ErrorProcessor() {

            @Override
            public void onError(String sessionId, Throwable error) {
                error.printStackTrace();
            }
        });
        
        // 注册服务供Client调用
        server.getServiceRegistry().register(new LoginServiceImpl());
        server.getServiceRegistry().register(new RegisterServiceImpl());
        
        System.out.println("Server is running...");
        execService.execute(new Runnable() {

            @Override
            public void run() {
                server.start();
            }
            
        });
    }
    
    static void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            server = null;
        }
        if (execService != null) {
            execService.shutdownNow();
            execService = null;
        }
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
    
    /**
     * 向指定的POS发送通知
     * 
     * @param posId
     * @param message
     * @throws Exception
     */
    public static void notifyMessage(String posId, String message) throws Exception {
        String sessionId = OnlinePosHolder.getSessionId(posId);
        WsonrpcRemote remote = Remotes.getRemote(sessionId);
        if (remote != null) {
            CallPosService callPosSrv = WsonrpcRemote.Executor.create(remote)
                    .getService(CallPosService.class);
            callPosSrv.notifyMessage(message);
        }
    }
    
    /**
     * 从指定的POS获取数据
     * 
     * @param posId
     * @param userId
     * @return
     * @throws Exception
     */
    public static User getUserFromPos(String posId, String userId) throws Exception {
        String sessionId = OnlinePosHolder.getSessionId(posId);
        WsonrpcRemote remote = Remotes.getRemote(sessionId);
        if (remote != null) {
            CallPosService callPosSrv = WsonrpcRemote.Executor.create(remote)
                    .getService(CallPosService.class);
            return callPosSrv.getPosUser(userId);
        }
        return null;
    }

}
