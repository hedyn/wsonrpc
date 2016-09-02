/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.apexes.wsonrpc.core.JsonRpcControl;
import net.apexes.wsonrpc.core.Transport;
import net.apexes.wsonrpc.core.WsonrpcException;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class JsonRpcHttpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected final JsonRpcControl jsonRpcControl;
    
    /**
     * 
     * @param jsonImpl
     */
    public JsonRpcHttpServlet(JsonImplementor jsonImpl) {
        this.jsonRpcControl = new JsonRpcControl(jsonImpl);
    }
    
    /**
     * 
     * @param name
     * @param handler
     * @param classes
     */
    public <T> void register(String name, T handler, Class<?>... classes) {
        jsonRpcControl.register(name, handler, classes);
    }
    
    /**
     * 
     * @param name
     */
    public <T> void unregister(String name) {
        jsonRpcControl.unregister(name);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            InputStream in = req.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            jsonRpcControl.receiveRequest(out.toByteArray(), new HttpServletTransport(resp));
        } catch (WsonrpcException e) {
            throw new ServletException(e);
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class HttpServletTransport implements Transport {
        
        private final HttpServletResponse httpResp;
        
        public HttpServletTransport(HttpServletResponse httpResp) {
            this.httpResp = httpResp;
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            httpResp.addHeader("Content-Type", "application/json; charset=utf-8");
            httpResp.setHeader("Content-Length", Integer.toString(bytes.length));
            OutputStream out = httpResp.getOutputStream();
            out.write(bytes);
            out.flush();
        }

    }

}
