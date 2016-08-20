package cn.net.xyan.blossom.platform.intercept;

import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public interface MethodInterceptor {
    boolean throwOutException();
    void setup(ApplicationContext ac);
    boolean accept(EndPoint endPoint);
    boolean needExec(Map<String,Object> content,Object result);
    Object exec(Map<String,Object> content,Object result);
}
