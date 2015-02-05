package net.apexes.wsonrpc.demo.server;

import java.net.InetSocketAddress;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;

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
        JavaWebsocketWsonrpcServer server = new JavaWebsocketWsonrpcServer(address);
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
