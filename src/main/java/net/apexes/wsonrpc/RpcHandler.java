/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface RpcHandler {

    void addService(String name, Object service);

    /**
     * 向远端发送一个RPC调用
     * @param id
     * @param methodName
     * @param argument
     * @param ops
     * @throws Exception
     */
    void invoke(String id, String methodName, Object argument, OutputStream ops) throws Exception;

    /**
     * 从输入流中读取一个RpcMessage
     * @param ips
     * @return
     * @throws Exception
     */
    RpcMessage readRpcMessage(InputStream ips) throws Exception;

    /**
     * 处理远端的RPC调用并将回复写到输出流中
     * @param value 远端调用传来的对象，即 JsonMessage.getValue()
     * @param ops 用于回复的输出流
     * @throws Exception
     */
    void handleInvoke(Object value, OutputStream ops) throws Exception;

    /**
     * 处理远端的RPC回复
     * @param value 远端回复的对象，即 JsonMessage.getValue()
     * @param returnType 返回的对象类型
     * @param callback
     * @throws Exception
     */
    void handleResult(Object value, Type returnType, Callback callback) throws Exception;

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class RpcMessage {

        private final String id;

        private final Object value;

        private final boolean invoked;

        private RpcMessage(String id, Object value, boolean invoked) {
            this.id = id;
            this.value = value;
            this.invoked = invoked;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @return the invoked
         */
        public boolean isInvoked() {
            return invoked;
        }

        public static RpcMessage createInvokeMessage(String id, Object value) {
            return new RpcMessage(id, value, true);
        }

        public static RpcMessage createResultMessage(String id, Object value) {
            return new RpcMessage(id, value, false);
        }
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static interface Callback {

        void result(Object value);

        void error(Throwable throwable);

    }
}
