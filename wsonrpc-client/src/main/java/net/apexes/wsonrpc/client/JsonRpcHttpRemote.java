/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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

import net.apexes.wsonrpc.core.JsonRpcControl;
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
    protected final JsonRpcControl jsonRpcControl;
    protected final Random rand;

    private int connectTimeout;
    private boolean acceptCompress;

    /**
     * 
     * @param url
     * @param jsonImpl
     */
    public JsonRpcHttpRemote(URL url, JsonImplementor jsonImpl) {
        this.url = url;
        this.headers = new HashMap<String, String>();
        this.jsonRpcControl = new JsonRpcControl(jsonImpl);
        rand = new Random();
    }

    /**
     * 
     * @param key
     * @param value
     */
    protected final void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public final void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public boolean isAcceptCompress() {
        return acceptCompress;
    }

    public void setAcceptCompress(boolean value) {
        acceptCompress = value;
    }

    @Override
    public void invoke(String handlerName, String methodName, Object[] args)
            throws IOException, WsonrpcException {
        TransportImpl transport = new TransportImpl(url, headers, connectTimeout, 0, acceptCompress);
        try {
            jsonRpcControl.invoke(handlerName, methodName, args, null, transport);
        } finally {
            transport.close();
        }
    }

    @Override
    public <T> T invoke(String handlerName, String methodName, Object[] args, Class<T> returnType,
            int timeout) throws IOException, WsonrpcException {
        TransportImpl transport = new TransportImpl(url, headers, connectTimeout, timeout, acceptCompress);
        try {
            int id = rand.nextInt(Integer.MAX_VALUE);
            jsonRpcControl.invoke(handlerName, methodName, args, String.valueOf(id), transport);
            return jsonRpcControl.receiveResponse(transport.readBinary(), returnType);
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

        TransportImpl(URL url, Map<String, String> headers, int connectTimeout, int readTimeout, boolean acceptCompress)
                throws IOException {
            this.conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (acceptCompress) {
                conn.setRequestProperty("Accept-Encoding", "gzip");
            }
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.flush();
        }

        public byte[] readBinary() throws IOException {
            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("unexpected status code returned : " + statusCode 
                        + ", message : " + conn.getResponseMessage());
            }
            String responseEncoding = conn.getHeaderField("Content-Encoding");
            responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());

            InputStream in = null;
            if ("gzip".equalsIgnoreCase(responseEncoding)) {
                in = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
            } else {
                in = new BufferedInputStream(conn.getInputStream());
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        }

        public void close() {
            conn.disconnect();
        }

    }

}
