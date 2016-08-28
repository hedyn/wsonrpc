/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.util.concurrent.Executor;

import net.apexes.wsonrpc.core.BinaryWrapper;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.support.GsonImplementor;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcServerConfigBuilder {
    
    public static WsonrpcServerConfigBuilder create() {
        return new WsonrpcServerConfigBuilder();
    }
    
    private JsonImplementor jsonImpl;
    private BinaryWrapper binaryWrapper;
    private Executor executor;
    
    private WsonrpcServerConfigBuilder() {}
    
    public WsonrpcServerConfig build() {
        if (jsonImpl == null) {
            jsonImpl = new GsonImplementor();
        }
        if (executor == null) {
            executor = new Executor() {

                @Override
                public void execute(Runnable runnable) {
                    runnable.run();
                }
                
            };
        }
        return new WsonrpcServerConfigImpl(jsonImpl, binaryWrapper, executor);
    }

    public WsonrpcServerConfigBuilder json(JsonImplementor jsonImpl) {
        this.jsonImpl = jsonImpl;
        return this;
    }

    public WsonrpcServerConfigBuilder binaryWrapper(BinaryWrapper binaryWrapper) {
        this.binaryWrapper = binaryWrapper;
        return this;
    }

    public WsonrpcServerConfigBuilder executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WsonrpcServerConfigImpl implements WsonrpcServerConfig {

        private JsonImplementor jsonImpl;
        private BinaryWrapper binaryWrapper;
        private Executor executor;

        private WsonrpcServerConfigImpl(JsonImplementor jsonImpl, BinaryWrapper binaryWrapper, Executor executor) {
            this.jsonImpl = jsonImpl;
            this.binaryWrapper = binaryWrapper;
            this.executor = executor;
        }

        @Override
        public JsonImplementor getJsonImplementor() {
            return jsonImpl;
        }

        @Override
        public BinaryWrapper getBinaryWrapper() {
            return binaryWrapper;
        }

        @Override
        public Executor getExecutor() {
            return executor;
        }

    }

}
