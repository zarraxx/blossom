package cn.net.xyan.blossom.declarative.service;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by zarra on 16/8/20.
 */
public interface DynamicBeanService extends BeanDefinitionRegistryPostProcessor, ApplicationContextAware, ApplicationListener<ApplicationEvent> {
    void refresh();
}
