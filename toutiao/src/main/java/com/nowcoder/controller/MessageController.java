package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.*;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//第六课有个很复杂的sql语句很有价值
@Controller
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class); //来自log库的log

@RequestMapping(path={"/msg/detail"},method={RequestMethod.GET})   //来自某个站内信的具体消息
public String conversationDetail(Model model, @Param("conversationId") String conversationId){
    try{
        List<Message> conversationList=messageService.getConversationDetail(conversationId,0,10);
        List<ViewObject> messages=new ArrayList<>();     //通过viewobject把站内详情页上拥有的其他东西传过去
        for(Message msg:conversationList){
            ViewObject vo=new ViewObject();
            vo.set("message",msg);
            User user=userService.getUser(msg.getFromId());
            if(user ==null){
                continue;
            }
            vo.set("headUrl",user.getHeadUrl());
            vo.set("userId",user.getId());
            messages.add(vo);
        }
        model.addAttribute("messages", messages);
        return "letterDetail";
    }catch(Exception e){
        logger.error("获取站内信详情失败"+e.getMessage());
    }
          return "letterDetail";
}
    @RequestMapping(path={"/msg/list"},method={RequestMethod.GET})   //来自某个站内信的具体消息
    public String conversationList(Model model){
        try{
         int localUserId=hostHolder.getUser().getId();
         List<Message> conversationList=messageService.getConversationList(localUserId,0,10);
         List<ViewObject> conversations=new ArrayList<>();
         for(Message msg:conversationList){
             ViewObject vo=new ViewObject();
             vo.set("conversation",msg);
             int targetId=msg.getFromId()==localUserId?msg.getToId():msg.getFromId();
             User user=userService.getUser(targetId);
             vo.set("headUrl", user.getHeadUrl());
             vo.set("userName", user.getName());
             vo.set("targetId", targetId);
             vo.set("totalCount", msg.getId());
             vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
             conversations.add(vo);
         }
            model.addAttribute("conversations", conversations);
            return "letter";
        }catch(Exception e){
            logger.error("获取站内信列表失败"+e.getMessage());
        }
        return "letter";
    }

@RequestMapping(path={"/msg/addMessage"},method = {RequestMethod.POST})      //站内信里面的各种消息
@ResponseBody
public String addMessage(@RequestParam("fromId") int fromId,
                         @RequestParam("toId") int toId,
                         @RequestParam("content") String content){
    try{
        Message msg=new Message();
        msg.setFromId(fromId);
        msg.setToId(toId);
        msg.setConversationId(fromId<toId?String.format("%d_%d",fromId,toId ):String.format("%d_%d",toId,fromId ) );
        msg.setContent(content);
        msg.setCreatedDate(new Date());
        messageService.addMessage(msg);
        return ToutiaoUtil.getJSONString(msg.getId());
    }catch(Exception e){
        logger.error("增加消息失败"+e.getMessage());
        return ToutiaoUtil.getJSONString(1,"增加消息失败");
    }

}
}
