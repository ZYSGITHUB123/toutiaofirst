package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;
   public Map<String,Object> register(String username, String password){
       Map<String,Object> map=new HashMap<String,Object>();
       if(StringUtils.isBlank(username)){
              map.put("msgname","用户名不能为空");
              return map;
       }
       if(StringUtils.isBlank(password)){
          map.put("msgpwd","密码不能为空");
          return map;
       }
       User user=userDAO.selectByName(username);
       if(user!=null){
           map.put("msgname","用户名已被注册");
           return map;
       }
       user=new User();
       user.setName(username);
       user.setSalt(UUID.randomUUID().toString().substring(0,5));
       user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
       user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
       userDAO.addUser(user);
       String ticket=addLoginTicket(user.getId());
       map.put("ticket",ticket);
       return map;
   }
    public Map<String,Object> login(String username, String password){
        Map<String,Object> map=new HashMap<String,Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user==null){
            map.put("msgname","用户名不存在");
            return map;
        }
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpasswd","密码不正确");
            return map;
        }
        //ticket,
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    private String addLoginTicket(int useId){
        LoginTicket ticket=new LoginTicket();
        ticket.setUserId(useId);
        ticket.setStatus(0);
        Date date=new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
    public User getUser(int id){
        return userDAO.selectById(id);
    }
}
