/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.apexes.wsonrpc.core.JsonRpcKernel;
import net.apexes.wsonrpc.core.Transport;
import net.apexes.wsonrpc.core.WsonrpcException;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class JsonRpcHttpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected final JsonRpcKernel jsonRpcKernel;
    
    /**
     * 
     * @param jsonImpl
     */
    public JsonRpcHttpServlet(JsonImplementor jsonImpl) {
        this(new JsonRpcKernel(jsonImpl));
    }
    
    /**
     * 
     * @param jsonRpcKernel
     */
    public JsonRpcHttpServlet(JsonRpcKernel jsonRpcKernel) {
        this.jsonRpcKernel = jsonRpcKernel;
    }
    
    /**
     * 
     * @param name
     * @param handler
     * @param classes
     */
    public <T> void register(String name, T handler, Class<?>... classes) {
        jsonRpcKernel.register(name, handler, classes);
    }
    
    /**
     * 
     * @param name
     */
    public <T> void unregister(String name) {
        jsonRpcKernel.unregister(name);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            jsonRpcKernel.receiveRequest(req.getInputStream(), new HttpServletTransport(resp));
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
