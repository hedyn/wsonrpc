package net.apexes.wsonrpc;

import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.internal.SimpleWsonrpcConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcConfig {

    ExecutorService getExecutorService();

    ObjectMapper getObjectMapper();

    BinaryWrapper getBinaryWrapper();

    /**
     * 返回超时时间，0表示永不超时。单位为TimeUnit.MILLISECONDS
     * 
     * @return
     */
    long getTimeout();

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    public static class Builder {

        public static WsonrpcConfig create(ExecutorService execService) {
            return create(execService, new ObjectMapper());
        }

        public static WsonrpcConfig create(ExecutorService execService, long timeout) {
            return create(execService, new ObjectMapper(), timeout);
        }

        public static WsonrpcConfig create(ExecutorService execService, ObjectMapper mapper) {
            return create(execService, mapper, 0);
        }

        public static WsonrpcConfig create(ExecutorService execService, ObjectMapper mapper, long timeout) {
            return new SimpleWsonrpcConfig(execService, mapper, timeout);
        }

    }

}
