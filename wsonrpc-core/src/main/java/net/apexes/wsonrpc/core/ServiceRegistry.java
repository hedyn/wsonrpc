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
public interface ServiceRegistry {
    
    <T> ServiceRegistry register(String name, T service, Class<?>... classes);
    
    <T> ServiceRegistry unregister(String name);

}
