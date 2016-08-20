package cn.net.xyan.blossom.platform.intercept.interceptor;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.RequestUtils;
import cn.net.xyan.blossom.platform.dao.RequestLogDao;
import cn.net.xyan.blossom.platform.entity.log.RequestLog;
import cn.net.xyan.blossom.platform.intercept.InterceptService;
import cn.net.xyan.blossom.platform.intercept.MethodInterceptor;
import cn.net.xyan.blossom.platform.intercept.annotation.NeedLogInterceptor;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class LogInterceptor extends AbstractMethodInterceptor{

    RequestLogDao requestLogDao;

    Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    ObjectMapper objectMapper;

    @Override
    public boolean throwOutException() {
        return false;
    }

    @Override
    public void setupProperties() {
        requestLogDao = getBean(RequestLogDao.class);
        objectMapper = new ObjectMapper();
        logger.info("requestLogDao:"+requestLogDao);
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

    public RequestLog requestLog(Map<String, Object> content){
        RequestLog log = new RequestLog(content.get(InterceptService.KEYTarget),
                (Method) content.get(InterceptService.KEYMethod));
        log.setRemoteIP(RequestUtils.remoteAddr());
        log.setType("request");
        try {
            Map<String, Object> c = new HashMap<>(content);
            c.remove(InterceptService.KEYTarget);
            c.remove(InterceptService.KEYMethod);
            log.setContent(objectMapper.writeValueAsString(c));
        } catch (JsonProcessingException e) {
            log.setContent(ExceptionUtils.errorString(e));
        }

        return log;
    }

    @Override
    public Object exec(Map<String, Object> content, Object result) {
        requestLogDao.saveAndFlush(requestLog(content));
        return result;
    }
}
