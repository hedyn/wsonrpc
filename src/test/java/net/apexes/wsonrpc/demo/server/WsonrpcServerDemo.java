package net.apexes.wsonrpc.demo.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.GsonJsonContext;
import net.apexes.jsonrpc.JsonRpcLogger;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.demo.api.CallPosService;
import net.apexes.wsonrpc.demo.api.User;
import net.apexes.wsonrpc.server.Remotes;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcServerDemo {
    
    public static void main(String[] args) {
        runServer();
    }
    
    private static ExecutorService execService;
    private static JavaWebsocketWsonrpcServer server;

    public static void runServer() {
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
        server = new JavaWebsocketWsonrpcServer(address, null, config);
        server.setExceptionProcessor(new ExceptionProcessor() {

            @Override
            public void onError(Throwable error, Object... params) {
                error.printStackTrace();
            }
        });
        
        // 注册服务供Client调用
        server.getServiceRegistry().register(new LoginServiceImpl());
        
        System.out.println("Server is running...");
        server.run();
    }
    
    public static void stopServer() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            server = null;
        }
        if (execService != null) {
            execService.shutdownNow();
            execService = null;
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
