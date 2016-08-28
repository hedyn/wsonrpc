/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.api;

import net.apexes.wsonrpc.demo.api.model.User;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface RegisterHandler {
    
    void register(String clientId);
    
    User login(String username, String password);

}
