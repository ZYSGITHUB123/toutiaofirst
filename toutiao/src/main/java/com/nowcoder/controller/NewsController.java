package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;


@Controller
public class NewsController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class); //来自log库的log
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