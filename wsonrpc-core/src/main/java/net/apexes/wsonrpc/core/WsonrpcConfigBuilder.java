/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.util.concurrent.Executor;

import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.support.GsonImplementor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class WsonrpcConfigBuilder {
    
    public static WsonrpcConfig defaultConfig() {
        return create().build();
    }
    
    public static WsonrpcConfigBuilder create() {
        return new WsonrpcConfigBuilder();
    }
    
    private JsonImplementor jsonImpl;
    private BinaryWrapper binaryWrapper;
    private Executor executor;
    
    private WsonrpcConfigBuilder() {}
    
    public WsonrpcConfig build() {
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
        return new WsonrpcConfig() {

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
            
        };
    }

    /**
     * 
     * @param jsonImpl
     * @return
     */
    public WsonrpcConfigBuilder json(JsonImplementor jsonImpl) {
        this.jsonImpl = jsonImpl;
        return this;
    }
    
    /**
     * 
     * @param binaryWrapper
     * @return
     */
    public WsonrpcConfigBuilder binaryWrapper(BinaryWrapper binaryWrapper) {
        this.binaryWrapper = binaryWrapper;
        return this;
    }

    /**
     * 
     * @param executor
     * @return
     */
    public WsonrpcConfigBuilder executor(Executor executor) {
        this.executor = executor;
        return this;
    }

}
