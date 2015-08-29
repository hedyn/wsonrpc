/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface ServiceMethodFinder extends ServiceRegistry {
    
    MethodHolder find(String methodName);
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    final class MethodHolder {
        
        public final Object service;
        
        public final Set<Method> methods;
        
        public MethodHolder(Object service, Set<Method> methods) {
            this.service = service;
            this.methods = methods;
        }
        
        public boolean hasMethod() {
            return methods != null && !methods.isEmpty();
        }
        
    }

}
