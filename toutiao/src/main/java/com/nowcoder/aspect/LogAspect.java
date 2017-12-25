package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component             //Component用来初始化
public class LogAspect {
    private static final Logger logger= LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.controller.IndexController.*(..))")    //在执行，，的之前  可以通过改条件，切点来控制切面的范围
    public void beforeMethod(JoinPoint joinpoint){
        StringBuilder sb=new StringBuilder();
        for(Object arg:joinpoint.getArgs()){
          sb.append("arg:"+arg.toString()+"|");
        }
      logger.info("before method:"+sb);    //info方法可以输出来
    }
    @After("execution(* com.nowcoder.controller.IndexController.*(..))")    //在执行，，的之后
    public void afterMethod(){
        logger.info("after method:");
    }
}
