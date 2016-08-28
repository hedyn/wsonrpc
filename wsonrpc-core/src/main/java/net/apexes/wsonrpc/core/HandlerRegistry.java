/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface HandlerRegistry {
    
    <T> HandlerRegistry register(String name, T handler, Class<?>... classes);
    
    <T> HandlerRegistry unregister(String name);

}
