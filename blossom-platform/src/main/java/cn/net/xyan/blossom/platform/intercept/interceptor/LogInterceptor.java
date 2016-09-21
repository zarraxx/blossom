package cn.net.xyan.blossom.platform.intercept.interceptor;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.RequestUtils;
import cn.net.xyan.blossom.platform.entity.log.RequestLog;
import cn.net.xyan.blossom.platform.intercept.InterceptService;

import cn.net.xyan.blossom.platform.intercept.annotation.NeedLogInterceptor;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;

import cn.net.xyan.blossom.platform.service.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class LogInterceptor extends AbstractMethodInterceptor{

    LogService logService;

    Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    ObjectMapper objectMapper;

    @Override
    public boolean throwOutException() {
        return false;
    }

    @Override
    public void setupProperties() {
        logService = getBean(LogService.class);
        objectMapper = new ObjectMapper();
        logger.info("requestLogDao:"+logService);
    }

    @Override
    public boolean accept(EndPoint endPoint) {
        Class<?> targetClass = endPoint.getTargetClass();
        Method method = endPoint.getMethod();



        if (targetClass.isAnnotationPresent(NeedLogInterceptor.class) ||
                method.isAnnotationPresent(NeedLogInterceptor.class))
            return true;

        return false;
    }

    @Override
    public boolean needExec(Map<String, Object> content, Object result) {
        return true;
    }

    public Map<String, Object> mapRemoveSystemKey(Map<String, Object> content ){
        Map<String, Object> c = new HashMap<>(content);
        c.remove(InterceptService.KEYTarget);
        c.remove(InterceptService.KEYMethod);
        return c;
    }

    public RequestLog requestLog(Map<String, Object> content){
        RequestLog log = new RequestLog(content.get(InterceptService.KEYTarget),
                (Method) content.get(InterceptService.KEYMethod));
        log.setRemoteIP(RequestUtils.remoteAddr());
        log.setType("request");
        try {
            Map<String, Object> c = mapRemoveSystemKey(content);
            log.setContent(objectMapper.writeValueAsString(c));
        } catch (JsonProcessingException e) {
            log.setContent(ExceptionUtils.errorString(e));
        }

        return log;
    }

    public RequestLog responseLog(Map<String, Object> content,Object rsp){
        RequestLog log =  requestLog(content);
        log.setType("response");

        Map<String,Object> body = new HashMap<>();
        Map<String, Object> request = mapRemoveSystemKey(content);

        body.put("request",request);
        body.put("response",rsp);
        log.setContent("");
        try {
            log.setContent(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            log.setContent(ExceptionUtils.errorString(e));
        }
        return log;
    }

    @Override
    public Object exec(Map<String, Object> content, Object result) {
        RequestLog log;
        if (result == null)
            log = requestLog(content);
        else
            log = responseLog(content,result);
        if (logService!=null)
            logService.saveLog(log);
        return result;
    }

    @Override
    public void onException(Map<String, Object> content, Throwable exception) {
        RequestLog log = responseLog(content,ExceptionUtils.errorString(exception));
        log.setContent("exception");
        if (logService!=null)
            logService.saveLog(log);
    }
}
