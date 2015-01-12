/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.api;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface CallClientService {
    
    String callClient(String msg);
    
    String[] callClient(User user);

}
