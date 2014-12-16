/**
 * Copyright © 2013-2014, Ratepo.com. All Rights Reserved.
 *
 * Reproduction by any means, or disclosure to parties who are not 
 * employees of Ratepo is forbidden unless authorized.
 */
package net.apexes.wsonrpc.demo.api;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class User {
    
    private String username;
    
    private String password;
    
    private Integer level;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + ", level=" + level + "]";
    }
    
}
