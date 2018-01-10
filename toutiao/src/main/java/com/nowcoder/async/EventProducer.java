package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){         //将事件放进队列
            try {
                String json = JSONObject.toJSONString(model);  //先将这个事件进行序列化
                String key = RedisKeyUtil.getEventQueueKey();
                jedisAdapter.lpush(key, json);              //再将事件推进队列
                return true;
            }catch(Exception e){
                return false;
            }
    }

}
