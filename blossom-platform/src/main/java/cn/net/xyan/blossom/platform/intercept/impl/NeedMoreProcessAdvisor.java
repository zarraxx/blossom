package cn.net.xyan.blossom.platform.intercept.impl;

import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * Created by zarra on 16/8/19.
 */
public class NeedMoreProcessAdvisor extends StaticMethodMatcherPointcutAdvisor {
    @Override
    public boolean matches(Method method, Class<?> aClass) {
        return false;
    }
}
