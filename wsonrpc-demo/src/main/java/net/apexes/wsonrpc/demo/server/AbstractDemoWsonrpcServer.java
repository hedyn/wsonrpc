/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.core.RemoteInvoker;
import net.apexes.wsonrpc.core.WsonrpcRemote;
import net.apexes.wsonrpc.demo.api.PushService;
import net.apexes.wsonrpc.server.WsonrpcRemotes;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class AbstractDemoWsonrpcServer implements DemoServer {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDemoWsonrpcServer.class);
    
    @Override
    public void ping(String clientId) {
        if (clientId == null) {
            for (WsonrpcRemote remote : WsonrpcRemotes.getRemotes()) {
                try {
                    remote.ping();
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        } else {
            WsonrpcRemote remote = findRemote(clientId);
            try {
                remote.ping();
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void call(String clientId) {
        if (clientId == null) {
            for (WsonrpcRemote remote : WsonrpcRemotes.getRemotes()) {
                call(remote);
            }
        } else {
            WsonrpcRemote remote = findRemote(clientId);
            call(remote);
        }
    }
    
    private void call(WsonrpcRemote remote) {
        if (remote != null) {
            PushService service = RemoteInvoker.create(remote).serviceName("push").get(PushService.class);
            String callParam = UUID.randomUUID().toString();
            LOG.info("callParam={}", callParam);
            String callValue = service.setupStatus(callParam);
            LOG.info("callValue={}", callValue);
        }
    }

    @Override
    public void notice(String message, String clientId) {
        if (clientId == null) {
            for (WsonrpcRemote remote : WsonrpcRemotes.getRemotes()) {
                notice(remote, message);
            }
        } else {
            WsonrpcRemote remote = findRemote(clientId);
            notice(remote, message);
        }
    }
    
    private void notice(WsonrpcRemote remote, String message) {
        if (remote != null) {
            PushService service = RemoteInvoker.create(remote).serviceName("push").get(PushService.class);
            service.notice(message);
        }
    }
    
    private WsonrpcRemote findRemote(String clientId) {
        String sessionId = OnlineClientHolder.getSessionId(clientId);
        return WsonrpcRemotes.getRemote(sessionId);
    }

}
