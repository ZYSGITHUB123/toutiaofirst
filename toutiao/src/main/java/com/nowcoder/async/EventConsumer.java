package com.nowcoder.async;


import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType,List<EventHandler>> config=new HashMap<EventType,List<EventHandler>>();//有了这个配置以后，当接收到事件时，我们可以找到它对应的事件处理的handler
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;
    @Override
    public void afterPropertiesSet() throws Exception {
     Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);  //把所有实现了EventHandler接口的类给找出来，即各种handler
     if(beans !=null){
         for (Map.Entry<String,EventHandler> entry:beans.entrySet()){                          //遍历
             List<EventType> eventTypes=entry.getValue().getSupportEventTypes();             //找到handler能处理的事件类型，进行登记，再通过config反向找handler
             for(EventType type: eventTypes){
                 if(!config.containsKey(type)){
                     config.put(type,new ArrayList<EventHandler>());
                 }
                 config.get(type).add(entry.getValue());       //把entry.getValue加到刚刚得到的List当中
             }
         }
     }                          //通过以上，在初始化时是知道哪些事件需要哪些handler进行处理的
          Thread thread=new Thread(new Runnable() {           //再开一个线程去取事件然后处理事件
              @Override
              public void run() {
                while(true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);      //取事件
                    for(String message:events){
                        if(message.equals(key)){       //第一个是key？
                            continue;
                        }
                        EventModel eventModel= JSON.parseObject(message,EventModel.class);

                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHandler handler:config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
              }
          });
           thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {      //可以将某个application记录下来
             this.applicationContext=applicationContext;
    }
}
