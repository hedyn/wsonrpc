/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 * @param <T>
 */
class HandlerEntry<T> {

    private final T handler;
    private final Map<String, Set<Method>> methods;

    public HandlerEntry(T handler, Class<?>... classes) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }

        if (classes.length == 0) {
            throw new IllegalArgumentException("none interface");
        }

        this.handler = handler;

        Map<String, Set<Method>> map = new HashMap<>();

        Class<?> handlerClass = handler.getClass();
        for (Class<?> clazz : classes) {
            if (!clazz.isInterface()) {
                throw new IllegalArgumentException("class should be an interface : " + clazz);
            }

            for (Method m : clazz.getMethods()) {
                String methodName = m.getName();
                Class<?>[] params = m.getParameterTypes();

                Method method;
                try {
                    method = handlerClass.getMethod(methodName, params);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                            "not implements method : " + methodName + argumentTypesToString(params));
                } catch (SecurityException e) {
                    throw new IllegalArgumentException(
                            "access error : " + methodName + argumentTypesToString(params), e);
                }
                Set<Method> set = map.get(methodName);
                if (set == null) {
                    set = new HashSet<>();
                    map.put(methodName, set);
                }
                set.add(method);
            }
        }

        this.methods = Collections.unmodifiableMap(map);
    }

    public T getHandler() {
        return handler;
    }

    public Set<Method> getMethods(String methodName) {
        return methods.get(methodName);
    }

    private static String argumentTypesToString(Class<?>[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                Class<?> c = argTypes[i];
                buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }
}
