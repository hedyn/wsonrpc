/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import net.apexes.wsonrpc.server.WsonrpcServer;
import net.apexes.wsonrpc.server.support.NettyWsonrpcServer;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class NettyDemoWsonrpcServer extends AbstractDemoWsonrpcServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(NettyDemoWsonrpcServer.class);
    
    private ExecutorService execService;
    private NettyWsonrpcServer server;
    
    @Override
    public WsonrpcServer create() {
        execService = Executors.newCachedThreadPool();
        WsonrpcConfig config = WsonrpcConfigBuilder.create()
                .json(new JacksonImplementor())
                .binaryWrapper(new net.apexes.wsonrpc.core.GZIPBinaryWrapper())
                .executor(execService)
                .build();
        server = new NettyWsonrpcServer(8080, "/wsonrpc", config);
        return server.getWsonrpcServer();
    }
    
    @Override
    public void startup() throws Exception {
        LOG.info("Startup...");
        execService.execute(new Runnable() {

            @Override
            public void run() {
                server.start();
            }
            
        });
    }

    @Override
    public void shutdown() throws Exception {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOG.error("", e);
            }
            server = null;
        }
        if (execService != null) {
            execService.shutdownNow();
            execService = null;
        }
    }

}
