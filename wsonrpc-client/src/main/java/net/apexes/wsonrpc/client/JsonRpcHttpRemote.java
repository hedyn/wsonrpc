/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import net.apexes.wsonrpc.core.JsonRpcKernel;
import net.apexes.wsonrpc.core.Remote;
import net.apexes.wsonrpc.core.Transport;
import net.apexes.wsonrpc.core.WsonrpcException;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcHttpRemote implements Remote {

    protected final URL url;
    protected final Map<String, String> headers;
    protected final JsonRpcKernel jsonRpcKernel;
    protected final Random rand;

    private int connectTimeout;

    /**
     * 
     * @param url
     * @param jsonImpl
     */
    public JsonRpcHttpRemote(URL url, JsonImplementor jsonImpl) {
        this.url = url;
        this.headers = new HashMap<String, String>();
        this.jsonRpcKernel = new JsonRpcKernel(jsonImpl);
        rand = new Random();
    }

    /**
     * 
     * @param key
     * @param value
     */
    public final void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public final void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    @Override
    public void invoke(String handlerName, String methodName, Object[] args)
            throws IOException, WsonrpcException {
        TransportImpl transport = new TransportImpl(url, headers, connectTimeout, 1);
        try {
            jsonRpcKernel.invoke(handlerName, methodName, args, null, transport);
        } finally {
            transport.close();
        }
    }

    @Override
    public <T> T invoke(String handlerName, String methodName, Object[] args, Class<T> returnType,
            int timeout) throws IOException, WsonrpcException {
        TransportImpl transport = new TransportImpl(url, headers, connectTimeout, timeout);
        try {
            int id = rand.nextInt(Integer.MAX_VALUE);
            jsonRpcKernel.invoke(handlerName, methodName, args, String.valueOf(id), transport);
            return jsonRpcKernel.receiveResponse(transport.getInputStream(), returnType);
        } finally {
            transport.close();
        }
    }

    @Override
    public <T> Future<T> asyncInvoke(String handleName, String methodName, Object[] args,
            Class<T> returnType) throws IOException, WsonrpcException {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class TransportImpl implements Transport {

        private final HttpURLConnection conn;

        TransportImpl(URL url, Map<String, String> headers, int connectTimeout, int readTimeout)
                throws IOException {
            this.conn = (HttpURLConnection) url.openConnection();
            ;
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.connect();
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.flush();
        }

        public InputStream getInputStream() throws IOException {
            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("unexpected status code returned : " + statusCode);
            }
            String responseEncoding = conn.getHeaderField("Content-Encoding");
            responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());

            InputStream in = null;
            if ("gzip".equalsIgnoreCase(responseEncoding)) {
                in = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
            } else {
                in = new BufferedInputStream(conn.getInputStream());
            }
            return in;
        }

        public void close() {
            conn.disconnect();
        }

    }

}
