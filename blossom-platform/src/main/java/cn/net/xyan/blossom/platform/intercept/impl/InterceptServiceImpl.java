package cn.net.xyan.blossom.platform.intercept.impl;

import cn.net.xyan.blossom.platform.intercept.InterceptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class InterceptServiceImpl implements InterceptService {

    BeanDefinitionRegistry registry;

    Logger logger = LoggerFactory.getLogger(InterceptServiceImpl.class);

    public BeanDefinitionBuilder beanDefinitionBuilder(Class<?> beanCls, Map<String,Object> propertyValues,Map<String,String> propertyRefs){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanCls).setLazyInit(true);
        if (propertyValues!=null) {
            for (String key : propertyValues.keySet()) {
                builder.addPropertyValue(key, propertyValues.get(key));
            }
        }
        if (propertyRefs!=null) {
            for (String key : propertyRefs.keySet()) {
                builder.addPropertyReference(key, propertyRefs.get(key));
            }
        }
        return builder;
    }

    public void registerBeanDefinition(String beanName,BeanDefinitionBuilder builder){
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;

        BeanDefinitionBuilder adviceBuilder = beanDefinitionBuilder(NeedMoreProcessAdvice.class,null,null);
        String  adviceBeanName = "needMoreProcessAdvice";
        registerBeanDefinition(adviceBeanName,adviceBuilder);

        Map<String,String> advisorParams = new HashMap<>();
        advisorParams.put("advice",adviceBeanName);

        BeanDefinitionBuilder advisorBuilder = beanDefinitionBuilder(NeedMoreProcessAdvisor.class,null,advisorParams);
        registerBeanDefinition("needMoreProcessAdvisor",advisorBuilder);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("postProcessBeanFactory");
    }
}
