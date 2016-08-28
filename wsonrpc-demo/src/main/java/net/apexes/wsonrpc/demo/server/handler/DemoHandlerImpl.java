/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server.handler;

import java.util.ArrayList;
import java.util.List;

import net.apexes.wsonrpc.demo.api.DemoHandler;
import net.apexes.wsonrpc.demo.api.model.Dept;
import net.apexes.wsonrpc.demo.api.model.Role;
import net.apexes.wsonrpc.demo.api.model.User;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoHandlerImpl implements DemoHandler {

    public DemoHandlerImpl() {
    }

    @Override
    public String echo(String text) {
        return text;
    }

    @Override
    public User login(String username, String password) {
        return DemoDatas.userList.get(0);
    }

    @Override
    public List<Role> getRoleList() {
        return DemoDatas.roleList;
    }

    @Override
    public Dept getDept(String name) {
        return DemoDatas.deptList.get(0);
    }

    @Override
    public List<Dept> getDeptList() {
        return DemoDatas.deptList;
    }

    @Override
    public List<User> listUser(List<String> usernames) {
        List<User> userList = new ArrayList<>();
        for (String username : usernames) {
            User user = DemoDatas.userFinder.get(username);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }
    
}
