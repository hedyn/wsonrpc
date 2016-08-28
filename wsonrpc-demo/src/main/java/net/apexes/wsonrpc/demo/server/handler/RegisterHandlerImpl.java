/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.core.WsonrpcRemote;
import net.apexes.wsonrpc.demo.api.RegisterHandler;
import net.apexes.wsonrpc.demo.api.model.User;
import net.apexes.wsonrpc.demo.server.OnlineClientHolder;
import net.apexes.wsonrpc.server.WsonrpcRemotes;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class RegisterHandlerImpl implements RegisterHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterHandlerImpl.class);

    @Override
    public void register(String clientId) {
        LOG.info("clientId={}", clientId);
        WsonrpcRemote remote = WsonrpcRemotes.getRemote();
        String sessionId = remote.getSessionId();
        OnlineClientHolder.register(clientId, sessionId);
    }

    @Override
    public User login(String username, String password) {
        LOG.info("username={}, password={}", username, password);
        return DemoDatas.userFinder.get(username);
    }

}
