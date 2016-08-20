package cn.net.xyan.blossom.platform.intercept.interceptor;

import cn.net.xyan.blossom.platform.dao.RequestLogDao;
import cn.net.xyan.blossom.platform.intercept.MethodInterceptor;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public abstract class AbstractMethodInterceptor implements MethodInterceptor {

    ApplicationContext ac;

    @Override
    public void setup(ApplicationContext ac) {
        this.ac = ac;
        setupProperties();
    }

    public abstract void setupProperties();

    public <T> T getBean(Class<T> tClass) {
        return BeanFactoryUtils.beanOfTypeIncludingAncestors(ac, tClass);
    }

    public <T> T getBean(String beanName,Class<T> tClass) {
        return ac.getBean(beanName,tClass);
    }

}
