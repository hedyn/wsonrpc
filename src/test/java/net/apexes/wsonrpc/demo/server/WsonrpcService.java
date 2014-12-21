/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import java.util.Arrays;
import java.util.concurrent.Executors;

import javax.websocket.server.ServerEndpoint;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.internal.WsonrpcServerEndpoint;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@ServerEndpoint("/wsonrpc")
public class WsonrpcService extends WsonrpcServerEndpoint implements ExceptionProcessor {

    public WsonrpcService() {
        super(WsonrpcConfig.Builder.create().build(Executors.newCachedThreadPool()));
        this.setExceptionProcessor(this);
        this.addService("loginService", new LoginServiceImpl());
    }

    @Override
    public void onError(Throwable throwable, Object... params) {
        if (params != null) {
            System.err.println(Arrays.toString(params));
        }
        throwable.printStackTrace();
    }
}
