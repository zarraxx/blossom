package cn.net.xyan.blossom.core.utils;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/14.
 */
public class ApplicationContextUtils {

    protected static ApplicationContextUtils _instance;

    ServletContext servletContext;

    WebApplicationContext webApplicationContext;


    private ApplicationContextUtils(){
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        webApplicationContext = ctx;
        _instance = this;
    }

    public static ApplicationContextUtils instance(){
        if (_instance == null){
            _instance = new ApplicationContextUtils();
        }
        return _instance;
    }

    static public void setServletContext(ServletContext servletContext) {
        instance().servletContext = servletContext;
    }

    static public void setApplicationContext(WebApplicationContext webApplicationContext) {
        instance().webApplicationContext = webApplicationContext;
    }

    public WebApplicationContext getApplicationContext() {
        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils
                    .getWebApplicationContext(servletContext);
        }
        return webApplicationContext;
    }

    public static <T> T getBean(Class<T> beanClass){
        WebApplicationContext webApplicationContext = instance().getApplicationContext();

        return webApplicationContext.getBean(beanClass);
    }

    public static Object getBean(String beanName){
        WebApplicationContext webApplicationContext = instance().getApplicationContext();

        return webApplicationContext.getBean(beanName);
    }

    static public String[] beanNamesForAnnotation(Class<? extends Annotation> annotation){
        WebApplicationContext webApplicationContext = instance().getApplicationContext();
        String[] beanNames = webApplicationContext.getBeanNamesForAnnotation(annotation);
        return beanNames;
    }

    static public String[] beanNamesForType(Class<?> cls){
        WebApplicationContext webApplicationContext = instance().getApplicationContext();
        String[] beanNames = webApplicationContext.getBeanNamesForType(cls);
        return beanNames;
    }

    static public Class<?> beanTypeForBeanName(String beanName){
        WebApplicationContext webApplicationContext = instance().getApplicationContext();
        return webApplicationContext.getType(beanName);
    }

    static public <T> List<Class<? extends  T>> beanTypesForType(Class<T> cls){
        String[] beanNames = beanNamesForType(cls);
        List<Class<? extends  T>> result = new LinkedList<>();
        for (String beanName:beanNames){
            Class<? extends T > beanType = (Class<? extends T>) beanTypeForBeanName(beanName);
            result.add(beanType);
        }
        return result;
    }

}


