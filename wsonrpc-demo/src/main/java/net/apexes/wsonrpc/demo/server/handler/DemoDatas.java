/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.apexes.wsonrpc.demo.api.model.Dept;
import net.apexes.wsonrpc.demo.api.model.Role;
import net.apexes.wsonrpc.demo.api.model.User;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoDatas {
    
    static List<Role> roleList;
    static List<User> userList;
    static List<Dept> deptList;
    static Map<String, User> userFinder;
    static {
        roleList = new ArrayList<>();
        Role role = new Role();
        role.setId(1);
        role.setName("管理员");
        roleList.add(role);role = new Role();
        role.setId(2);
        role.setName("维护员");
        roleList.add(role);role = new Role();
        role.setId(3);
        role.setName("普通员工");
        roleList.add(role);
        
        userList = new ArrayList<>();
        User user = new User();
        user.setUsername("admin");
        user.setName("管理员");
        user.setAmount(BigDecimal.valueOf(12.45));
        user.setRoleList(roleList.subList(0, 2));
        userList.add(user);
        user = new User();
        user.setUsername("1001");
        user.setName("维护01");
        user.setAmount(BigDecimal.valueOf(14));
        user.setRoleList(roleList.subList(1, 3));
        userList.add(user);
        user = new User();
        user.setUsername("2001");
        user.setName("普通员工01");
        userList.add(user);
        
        deptList = new ArrayList<>();
        Dept dept = new Dept();
        dept.setName("1部");
        dept.setUserList(userList.subList(0, 3));
        dept.setRoleList(roleList.subList(1, 2));
        deptList.add(dept);
        dept = new Dept();
        dept.setName("2部");
        dept.setUserList(userList.subList(1, 3));
        dept.setRoleList(roleList.subList(0, 2));
        deptList.add(dept);
        
        userFinder = new HashMap<>();
        for (User u : userList) {
            userFinder.put(u.getUsername(), u);
        }
    }

}
