/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.json;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonImplementor {
    
    /**
     * 将文本反序列化为{@link Node}对象
     * 
     * @param json
     * @return
     * @throws Exception
     */
    Node fromJson(String json) throws Exception;
    
    /**
     * 将{@link Node}对象序列化为文本
     * 
     * @param node
     * @return
     * @throws Exception
     */
    String toJson(Node node) throws Exception;
    
    /**
     * 创建一个{@link Node}对象
     * 
     * @return
     */
    Node createNode();
    
    /**
     * 判断指定的{@link Node}对象是否与指定类型匹配
     * @param node
     * @param classType
     * @return 如果匹配返回true，否则返回false
     */
    boolean isCompatible(Node node, Class<?> classType);
    
    /**
     * 将{@link Node}对象转为指定类型的对象
     * 
     * @param node
     * @param classType
     * @return
     */
    <T> T convert(Node node, Class<T> classType);
    
    /**
     * 将指定类型的对象转为{@link Node}对象
     * 
     * @param object
     * @return
     */
    Node convert(Object object);
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    interface Node {
        
        /**
         * 
         * @param name
         * @return
         */
        boolean has(String name);
        
        /**
         * 
         * @param name
         * @return
         */
        Node get(String name);
        
        /**
         * 
         * @param name
         * @return
         */
        Integer getInteger(String name);
        
        /**
         * 
         * @param name
         * @return
         */
        String getString(String name);
        
        /**
         * 
         * @param name
         * @return
         */
        Node[] getArray(String name);
        
        /**
         * 
         * @param name
         * @param value
         */
        void put(String name, int value);
        
        /**
         * 
         * @param name
         * @param value
         */
        void put(String name, String value);
        
        /**
         * 
         * @param name
         * @param value
         */
        void put(String name, Node value);
        
        /**
         * 
         * @param name
         * @param array
         */
        void put(String name, Node[] array);

    }
    
}
