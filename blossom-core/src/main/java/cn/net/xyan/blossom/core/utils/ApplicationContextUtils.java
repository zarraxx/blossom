package cn.net.xyan.blossom.core.utils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletContext;

/**
 * Created by zarra on 16/5/14.
 */
public class ApplicationContextUtils implements ServletContextAware,InitializingBean {

    protected static ApplicationContextUtils _instance;

    ServletContext servletContext;

    WebApplicationContext webApplicationContext;

     public static <T> T getBean(Class<T> beanClass){
        WebApplicationContext webApplicationContext = _instance.getApplicationContext();

        return webApplicationContext.getBean(beanClass);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        _instance = this;
    }


    public WebApplicationContext getApplicationContext() {
        if (webApplicationContext == null) {
            webApplicationContext = WebApplicationContextUtils
                    .getWebApplicationContext(servletContext);
        }
        return webApplicationContext;
    }

}


