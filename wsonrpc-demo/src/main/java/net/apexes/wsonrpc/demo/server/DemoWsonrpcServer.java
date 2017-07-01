/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcSession;
import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.api.RegisterService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;
import net.apexes.wsonrpc.demo.server.service.RegisterServiceImpl;
import net.apexes.wsonrpc.server.WsonrpcServer;
import net.apexes.wsonrpc.server.WsonrpcServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoWsonrpcServer {
    private static final Logger LOG = LoggerFactory.getLogger(DemoWsonrpcServer.class);
    
    public static void main(String[] args) throws Exception {
        DemoServer demoServer = null;
//        demoServer = new JwsDemoWsonrpcServer();
        demoServer = new TyrusDemoWsonrpcServer();
//        demoServer = new NettyDemoWsonrpcServer();
        runServer(demoServer);
    }
    
    protected static void runServer(DemoServer demoServer) throws Exception {
        WsonrpcServer serverBase = demoServer.create();
        if (serverBase != null) {
            setupServer(serverBase);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(">");
            String command = reader.readLine();
            if (command.isEmpty()) {
                continue;
            }
            if ("startup".equalsIgnoreCase(command)) {
                demoServer.startup();
            } else if ("shutdown".equalsIgnoreCase(command)) {
                demoServer.shutdown();
                LOG.info("Server is shutdown");
                System.exit(0);
                break;
            } else if (command.startsWith("call ")) {
                String[] cmds = command.split(" ");
                demoServer.call(cmds[1]);
            } else if (command.startsWith("notice ")) {
                String[] cmds = command.split(" ");
                if (cmds.length >= 3) {
                    demoServer.notice(cmds[1], cmds[2]);
                } else {
                    demoServer.notice(cmds[1], null);
                }
            } else if (command.equals("ping")) {
                demoServer.ping(null);
            } else if (command.startsWith("ping ")) {
                String[] cmds = command.split(" ");
                demoServer.ping(cmds[1]);
            }
        }
    }
    
    protected static void setupServer(WsonrpcServer serverBase) {
        serverBase.setServerListener(new WsonrpcServerListener() {

            @Override
            public void onOpen(WsonrpcSession session) {
                LOG.info("sessionId={}", session.getId());
            }

            @Override
            public void onClose(String sessionId) {
                LOG.info("sessionId={}", sessionId);
                OnlineClientHolder.unregister(sessionId);
            }

            @Override
            public void onMessage(String sessionId, byte[] bytes) {
                LOG.info("sessionId={}, length={}", sessionId, bytes.length);
            }
            
        });
        
        // 注册服务供Client调用
        serverBase.getServiceRegistry().register("demo", new DemoServiceImpl(), DemoService.class);
        serverBase.getServiceRegistry().register("register", new RegisterServiceImpl(), RegisterService.class);
    }

}
