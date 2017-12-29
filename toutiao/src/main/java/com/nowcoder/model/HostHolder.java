package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private  static  ThreadLocal<User> users=new ThreadLocal<User>();  //线程本地变量

    public void setUser(User user){            //每一个user设置一个
        users.set(user);
    }
    public  User getUser(){
        return users.get();               //只取自己对应的user
    }
    public void clear(){
        users.remove();
    }
}
