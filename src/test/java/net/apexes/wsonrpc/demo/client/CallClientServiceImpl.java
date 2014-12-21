/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client;

import net.apexes.wsonrpc.demo.api.CallClientService;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class CallClientServiceImpl implements CallClientService {
    
    @Override
    public String callClient(String msg) {
        return "Client result: " + msg;
    }

}
