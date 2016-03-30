package net.apexes.wsonrpc.demo.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import net.apexes.jsonrpc.GsonJsonContext;
import net.apexes.jsonrpc.JsonRpcLogger;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.server.WsonrpcServerListener;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer.PathStrategy;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JavaWebsocketServerDemo {
    
    public static void main(String[] args) {
        runServer();
    }

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
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonContext(jsonContext)
                .build(Executors.newCachedThreadPool());
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
                System.out.println("::onMessage: " + sessionId + ", length=" + bytes.length);
            }
        });
        server.getServiceRegistry().register(new LoginServiceImpl());
        server.getServiceRegistry().register(new RegisterServiceImpl());
        System.out.println("Server is running...");
        server.run();
    }

}
