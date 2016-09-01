/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.demo.api.PushService;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class PushServiceImpl implements PushService {
    
    private static final Logger LOG = LoggerFactory.getLogger(PushServiceImpl.class);

    @Override
    public String setupStatus(String value) {
        return "clientStatus : {" + value + "}";
    }

    @Override
    public void notice(String message) {
        LOG.info("onNotice: {}", message);
    }

}
