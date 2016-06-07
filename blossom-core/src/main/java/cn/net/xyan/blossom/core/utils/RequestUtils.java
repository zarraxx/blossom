package cn.net.xyan.blossom.core.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zarra on 16/6/8.
 */
public class RequestUtils {



    public static HttpServletRequest httpServletRequest(){
        try {
            HttpServletRequest curRequest =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();
            return curRequest;
        }catch (Throwable e){
            return null;
        }
    }

    public static String contextPath(){
        HttpServletRequest request = httpServletRequest();
        String contextPath = request.getContextPath();
        if ("/".equals(contextPath)){
            contextPath = "";
        }
        return contextPath;
    }

    public static String appRootURL(){
        HttpServletRequest request = httpServletRequest();
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean needPort = true;
        if ( ("http".equals(scheme) && port ==80)||
                ("https".equals(scheme) && port == 443)){
            needPort = false;
        }

        String sPort = String.format(":%d",port);
        if (needPort == false)
            sPort = "";

        return String.format("%s://%s%s%s",scheme,host,sPort,contextPath());
    }
}
