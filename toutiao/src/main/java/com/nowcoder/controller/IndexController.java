package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class); //来自log库的log
    @Autowired
    private ToutiaoService toutiaoService;     //控制反转，会自动搜索整个包中注册为该Service的，方便
    @RequestMapping(value={"/","index"})
    @ResponseBody
    public String  index(HttpSession session){
        logger.info("Visit Index");
        return "hello nowcoder"+session.getAttribute("msg")+"<br> Say:"+
                toutiaoService.say();
    }
    @RequestMapping(path={"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value="type" ,defaultValue="1") int type,  //这里的都是url后面一堆无序的
                          @RequestParam(value="key",defaultValue="nowcoder") String key){
        //这里会调用service或者dao层
        return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}",groupId,userId,type,key);

    }
    @RequestMapping(value={"/html"})
    public String news(Model model){
        model.addAttribute("value1","vv1");  //模型与模板交互，模型把value1传给模板

        List<String> colors= Arrays.asList(new String[] {"red","green","blue","yellow"});
        Map<String ,String> map=new HashMap<String,String>();
        for(int i=0;i<4;i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute ("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("Jim"));
        return "news";
    }
    @RequestMapping(value={"/request"})
    @ResponseBody
    public String request(HttpServletRequest request            //这里的http。。不是spring的而是一个规范
                           ){                             //可以通过request获取请求有的东西
        StringBuilder sb=new StringBuilder();
        Enumeration<String> headerNames=request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name=headerNames.nextElement();
            sb.append(name+":"+request.getHeader(name)+"<br>");
        }
        for(Cookie cookie:request.getCookies()){
            sb.append("Cookie");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }
        sb.append("getMethod:"+request.getMethod()+"<br>");
        sb.append("getPathInfo:"+request.getPathInfo()+"<br>");
        sb.append("getQueryString:"+request.getQueryString()+"<br>");
        sb.append("getRequestURl:"+request.getRequestURL()+"<br>");
        return sb.toString();

    }
    @RequestMapping(value={"/response"})
    @ResponseBody
    public String response(@CookieValue(value="nowcoderid",defaultValue = "a") String nowcoder,           //可以通过reponse写回去
                           @RequestParam(value="key",defaultValue="key") String key,
                           @RequestParam(value="value",defaultValue="value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "NowCoderId From  Cookie:"+nowcoder;

    }
    //重定向
    @RequestMapping(value="/redirect/{code}")

    public String redirect(@PathVariable("code") int code,
                           HttpSession session){
//        RedirectView red=new RedirectView("/",true);
//        if(code==301){
//            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        }
//        return red;
        session.setAttribute("msg","jump from redirect");
        return "redirect:/";

    }
    //如果输入的url错误做一个统一的处理
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value="key",required = false) String key){
        if("admin".equals(key)){
            return "hello admin ";
        }
        throw new   IllegalArgumentException("key 错误");
    }
    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error: "+ e.getMessage();
    }

}
