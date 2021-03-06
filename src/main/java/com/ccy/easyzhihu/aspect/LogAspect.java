package com.ccy.easyzhihu.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author chengcongyue
 * @version 1.0
 * @description com.ccy.easyzhihu.aspect
 * @date 2019/3/27
 */
@Aspect
@Component
public class LogAspect {

     private static final Logger logger= LoggerFactory.getLogger(LogAspect.class);
     @Before("execution(* com.ccy.easyzhihu.controller.*Controller.*(..))")
     public void beforeMethod(JoinPoint joinPoint)
     {
         logger.info("before method!!!");
     }

    @After("execution(* com.ccy.easyzhihu.controller.*Controller.*(..))")
    public void afterMethod()
    {
        logger.info("after method!!!");
    }
}
