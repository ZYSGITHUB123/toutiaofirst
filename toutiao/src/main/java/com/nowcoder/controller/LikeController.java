package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;
    @Autowired
    EventProducer eventProducer;



    @RequestMapping(path={"/like"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId){
          int userId=hostHolder.getUser().getId();
          long likeCount=likeService.like(userId, EntityType.Entity_News,newsId);  //统计like中集合元素的数目
        News news=newsService.getById(newsId);
         newsService.updateLikeCount(newsId,(int) likeCount);//news中有个likeCount字段要同步
         eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
                 .setEntityId(newsId).setEntityType(EntityType.Entity_News)
                 .setEntityOwnerId(news.getUserId()));
         return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));

    }
    @RequestMapping(path={"/dislike"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId){
        int userId=hostHolder.getUser().getId();
        long likeCount=likeService.disLike(userId, EntityType.Entity_News,newsId);  //统计like中集合元素的数目
        newsService.updateLikeCount(newsId,(int) likeCount);//news中有个likeCount字段要同步
        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));

    }



}
