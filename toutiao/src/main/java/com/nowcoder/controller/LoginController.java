package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class); //来自log库的log

    @Autowired
    UserService userService;

    @RequestMapping(path={"/reg/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody       //注意！！包好
    public String reg(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="rember",defaultValue = "0") int remember,
                         HttpServletResponse response){
         try{
             Map<String,Object> map=userService.register(username,password);
             if (map.containsKey("ticket")) {
                 Cookie cookie =new Cookie("ticket",map.get("ticket").toString());
                 cookie.setPath("/");     //路径为全栈有效
                 if(remember>0){
                     cookie.setMaxAge(3600*24*5);          //设置记住的时间
                 }
                 response.addCookie(cookie);            //添加cookie
                 return ToutiaoUtil.getJSONString(0,"注册成功");
             }else{
                 return ToutiaoUtil.getJSONString(1,map);
             }

         }catch(Exception e){
              logger.error("注册异常"+e.getMessage());
              return ToutiaoUtil.getJSONString(1,"注册异常");
         }

}
    @RequestMapping(path={"/login/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody       //注意！！包好
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember",defaultValue = "0") int remember
                      ){
        try{
            Map<String,Object> map=userService.login(username,password);
            if (map.containsKey("ticket")) {
                Cookie cookie =new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");     //路径为全栈有效
                if(remember>0){
                    cookie.setMaxAge(3600*24*5);          //设置记住的时间
                }
                return ToutiaoUtil.getJSONString(0,"登录成功");
            }else{
                return ToutiaoUtil.getJSONString(1,map);
            }

        }catch(Exception e){
            logger.error("登录异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"登录异常");
        }

    }
    @RequestMapping(path={"/logout/"},method = {RequestMethod.GET,RequestMethod.POST})

    public String logout(@CookieValue("ticket") String ticket){
      userService.logout(ticket);
      return  "redirect:/";
    }

}
