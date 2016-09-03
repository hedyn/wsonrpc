/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;
import net.apexes.wsonrpc.server.support.HttpJsonRpcServer;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoJsonRpcServer {

    private static final Logger LOG = LoggerFactory.getLogger(DemoJsonRpcServer.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("...");
        HttpJsonRpcServer server = new HttpJsonRpcServer(8080);
//        HttpJsonRpcServer server = new HttpJsonRpcServer(8080, new JacksonImplementor());
        server.getServiceRegistry().register("demo", new DemoServiceImpl(), DemoService.class);
        server.start(HttpJsonRpcServer.SOCKET_READ_TIMEOUT, false);
    }

}
