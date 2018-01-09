package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class NewsController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class); //来自log库的log

    @RequestMapping(path={"/news/{newsId}"},method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId,Model model){
        News news=newsService.getById(newsId);


        if(news!=null){
            int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
            if(localUserId!=0){
                model.addAttribute("like",likeService.getLikeStatus(localUserId, EntityType.Entity_News,news.getId()));
            }else{
                model.addAttribute("like",0);
            }
            List<Comment> comments=commentService.getCommentsByEntity(news.getId(), EntityType.Entity_News);
            List<ViewObject> commentVOs=new ArrayList<ViewObject>();
            for(Comment comment:comments){
                     ViewObject vo=new ViewObject();
                     vo.set("comment" ,comment);
                     vo.set("user",userService.getUser(comment.getUserId()));
                     commentVOs.add(vo);
            }
            model.addAttribute("comments",commentVOs);
        }
        model.addAttribute("news",news);
        model.addAttribute("owner",userService.getUser(news.getUserId()));
        return "detail";
    }
    @RequestMapping(path={"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String Content){
        try{
            //过滤敏感词
          Comment comment=new Comment();
          comment.setUserId(hostHolder.getUser().getId());
          comment.setContent(Content);
          comment.setEntityId(newsId);
          comment.setEntityType(EntityType.Entity_News);
          comment.setCreatedDate(new Date());
          comment.setStatus(0);


          commentService.addComment(comment);
          //更新news评论数量
            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());  //统计下面的评论数
            newsService.updateCommentCount(comment.getEntityId(),count);   //news显示上更新评论数量
            //怎么异步化（AJAX）


        }catch(Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }

    @RequestMapping(path={"/image"},method = {RequestMethod.GET})
    @ResponseBody
    public  void getImage(@RequestParam("name") String imageName,
                            HttpServletResponse response){
        try{
            response.setContentType("image/jpg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR+imageName)),response.getOutputStream());
        }catch(Exception e){
           logger.error("读取图片错误"+e.getMessage());
        }
    }
@RequestMapping(path={"/user/addNews"},method={RequestMethod.POST})
@ResponseBody
public String addNews(@RequestParam("image") String image,
                      @RequestParam("title") String title,
                      @RequestParam("link") String link){
        try{
            News news=new News();
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //匿名用户id,未登录状态
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
           newsService.addNews(news);
           return  ToutiaoUtil.getJSONString(0);  //成功
        }catch(Exception e){
            logger.error("添加资讯错误"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }
}

    @RequestMapping(path={"/uploadImage/"},method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try{
         String fileUrl=newsService.saveImage(file);
         if(fileUrl==null){
             return ToutiaoUtil.getJSONString(1,"图片上传失败");
         }
         return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch (Exception e){
          logger.error("上传图片失败"+e.getMessage());
          return ToutiaoUtil.getJSONString(1,"上传失败");
        }
    }
}
