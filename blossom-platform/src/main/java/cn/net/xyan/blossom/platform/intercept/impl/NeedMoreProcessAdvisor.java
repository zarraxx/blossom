package cn.net.xyan.blossom.platform.intercept.impl;

import cn.net.xyan.blossom.platform.intercept.InterceptService;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * Created by zarra on 16/8/19.
 */
public class NeedMoreProcessAdvisor extends StaticMethodMatcherPointcutAdvisor {
    Logger logger = LoggerFactory.getLogger(NeedMoreProcessAdvisor.class);

    InterceptService interceptService;

    public InterceptService getInterceptService() {
        return interceptService;
    }

    public void setInterceptService(InterceptService interceptService) {
        this.interceptService = interceptService;
    }

    @Override
    public boolean matches(Method method, Class<?> aClass) {
        if (Object.class.equals(method.getDeclaringClass()))
            return false;
        logger.info("bean class:"+aClass.getName());
        logger.info("class:"+method.getDeclaringClass().getName() + " method:"+method.getName());

        boolean b =  getInterceptService().accept(method,aClass);
        if (b){
            //logger.info("bean class:"+aClass.getName());
            //logger.info("class:"+method.getDeclaringClass().getName() + " method:"+method.getName());
        }

        return b;
    }
}
