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
    
    /**
     * 
     * @param name
     * @param service
     * @param classes
     * @return
     */
    <T> ServiceRegistry register(String name, T service, Class<?>... classes);

    /**
     * 
     * @param name
     * @return
     */
    ServiceRegistry unregister(String name);

}
