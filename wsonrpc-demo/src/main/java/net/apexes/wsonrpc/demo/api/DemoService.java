/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.api;

import java.util.List;

import net.apexes.wsonrpc.demo.api.model.Dept;
import net.apexes.wsonrpc.demo.api.model.Role;
import net.apexes.wsonrpc.demo.api.model.User;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface DemoService {

    String echo(String text);
    
    User login(String username, String password);
    
    List<Role> getRoleList();
    
    Dept getDept(String name);
    
    List<Dept> getDeptList();
    
    List<User> listUser(List<String> usernames);
}
