/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.demo.api.PushHandler;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class PushHandlerImpl implements PushHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushHandlerImpl.class);

    @Override
    public String setupStatus(String value) {
        return "clientStatus : {" + value + "}";
    }

    @Override
    public void notice(String message) {
        LOG.info("onNotice: {}", message);
    }

}
