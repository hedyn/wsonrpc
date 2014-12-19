/**
 * Copyright (C) 2014, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface RpcHandler {

    void addService(String name, Object service);

    void invoke(String id, String methodName, Object argument, OutputStream ops) throws Exception;

    JsonMessage toJsonMessage(InputStream ips) throws Exception;

    void handleRequest(Object value, OutputStream ops) throws Exception;

    void handleResponse(Object value, Type returnType, Callback callback) throws Exception;

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class JsonMessage {

        private final String id;

        private final Object value;

        private final boolean request;

        private JsonMessage(String id, Object value, boolean request) {
            this.id = id;
            this.value = value;
            this.request = request;
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
         * @return the request
         */
        public boolean isRequest() {
            return request;
        }

        public static JsonMessage createRequest(String id, Object value) {
            return new JsonMessage(id, value, true);
        }

        public static JsonMessage createResponse(String id, Object value) {
            return new JsonMessage(id, value, false);
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
