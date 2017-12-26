package com.nowcoder.controller;

import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NewsService newsService;
    @Autowired
    UserService userService;
    private List<ViewObject> getNews(int userId,int offset,int limit){
        List<News> newsList=newsService.getLatestNews(userId,offset,limit);

        List<ViewObject> vos=new ArrayList<>();
        for(News news:newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getId()));
            vos.add(vo);                          //通过viewobject方便的把news和user放进去

        }
        return vos;
    }
    @RequestMapping(path={"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model){
//        List<News> newsList=newsService.getLatestNews(0,0,10);  //传进来十个，但是一条资讯要对应一个用户
//
//        List<ViewObject> vos=new ArrayList<>();
//        for(News news:newsList){
//            ViewObject vo=new ViewObject();
//            vo.set("news",news);
//            vo.set("user",userService.getUser(news.getId()));
//            vos.add(vo);
//        }                           //通过viewobject方便的把news和user放进去
        model.addAttribute("vos",getNews(0,0,10));

        return "home";                //返回一个home模板

}
    @RequestMapping(path={"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userindex(Model model, @PathVariable("userId") int userId){
//        List<News> newsList=newsService.getLatestNews(0,0,10);  //传进来十个，但是一条资讯要对应一个用户
//
//        List<ViewObject> vos=new ArrayList<>();
//        for(News news:newsList){
//            ViewObject vo=new ViewObject();
//            vo.set("news",news);
//            vo.set("user",userService.getUser(news.getId()));
//            vos.add(vo);
//        }                           //通过viewobject方便的把news和user放进去
        model.addAttribute("vos",getNews(userId,0,10));       //具体到某个用户的页面

        return "home";                //返回一个home模板
    }
}
