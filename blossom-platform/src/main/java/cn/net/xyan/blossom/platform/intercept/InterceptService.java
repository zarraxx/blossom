package cn.net.xyan.blossom.platform.intercept;

import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public interface InterceptService extends BeanDefinitionRegistryPostProcessor {
     String KEYTarget = "__target__";
     String KEYMethod = "__method__";

     Object doIntercept(EndPoint endPoint, Map<String,Object> context, Object result);
     boolean accept(Method method, Class<?> aClass) ;
}
