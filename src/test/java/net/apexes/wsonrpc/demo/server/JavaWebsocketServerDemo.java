package net.apexes.wsonrpc.demo.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;
import net.apexes.wsonrpc.support.JacksonJsonHandler;
import net.apexes.wsonrpc.support.JsonLogger;

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
        JacksonJsonHandler jsonHandler = new JacksonJsonHandler();
        jsonHandler.setLogger(new JsonLogger() {

            @Override
            public void onRead(String json) {
                System.err.println("onRead: " + json);
            }

            @Override
            public void onWrite(String json) {
                System.err.println("onWrite: " + json);
            }
        });
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonHandler(jsonHandler)
                .build(Executors.newCachedThreadPool());
        JavaWebsocketWsonrpcServer server = new JavaWebsocketWsonrpcServer(address, config);
        server.setExceptionProcessor(new ExceptionProcessor() {

            @Override
            public void onError(Throwable error, Object... params) {
                error.printStackTrace();
            }
        });
        server.register(new LoginServiceImpl());
        System.out.println("Server is running...");
        server.run();
    }

}
