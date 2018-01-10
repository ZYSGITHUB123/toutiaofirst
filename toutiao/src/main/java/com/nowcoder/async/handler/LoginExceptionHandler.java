package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Component;

import java.util.*;


@Component   //实例化
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {   //怎么处理的
         //判断是否有异常登录
        Message message=new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登录ip异常");
        message.setFromId(3);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"),"登录异常","mails/welcome.html",map);


    }

    @Override
    public List<EventType> getSupportEventTypes() {    //具体关注哪些事件
        return Arrays.asList(EventType.LOGIN);    //注册了LOGIN
    }
}
