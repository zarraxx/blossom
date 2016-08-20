package cn.net.xyan.blossom.platform.intercept.impl;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.platform.intercept.MethodInterceptor;
import cn.net.xyan.blossom.platform.intercept.InterceptService;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class InterceptServiceImpl implements InterceptService,ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    BeanDefinitionRegistry registry;

    ApplicationContext applicationContext;

    Logger logger = LoggerFactory.getLogger(InterceptServiceImpl.class);

    Map<EndPoint,List<MethodInterceptor>> endPointAcceptCache = new HashMap<>();

    @Autowired
    List<MethodInterceptor> processorCache;

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



        InterceptService interceptService = applicationContext.getBean(InterceptService.class);
        BeanDefinitionBuilder adviceBuilder = beanDefinitionBuilder(NeedMoreProcessAdvice.class,null,null);
        adviceBuilder.addPropertyValue("interceptService",interceptService);
        String  adviceBeanName = "needMoreProcessAdvice";
        registerBeanDefinition(adviceBeanName,adviceBuilder);

        Map<String,String> advisorParams = new HashMap<>();
        advisorParams.put("advice",adviceBeanName);

        BeanDefinitionBuilder advisorBuilder = beanDefinitionBuilder(NeedMoreProcessAdvisor.class,null,advisorParams);
        advisorBuilder.addPropertyValue("interceptService",interceptService);
        registerBeanDefinition("needMoreProcessAdvisor",advisorBuilder);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String,MethodInterceptor> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,MethodInterceptor.class);
        if (processorCache == null)
            processorCache = new LinkedList<>();

        processorCache.addAll(map.values());

        logger.info("postProcessBeanFactory");
    }


    public Object doInterceptExec(MethodInterceptor p, Map<String, Object> context, Object result){
        try{
            result = p.exec(context,result);
        }catch (Throwable e){

            logger.error(ExceptionUtils.errorString(e));

            if (p.throwOutException())
                throw e;

        }
        return result;
    }

    @Override
    public Object doIntercept(EndPoint endPoint, Map<String, Object> context, Object result) {
        List<MethodInterceptor> processors = endPointAcceptCache.get(endPoint);

        if (processors == null){
            processors = new LinkedList<>();
            for (MethodInterceptor p:this.processorCache){
                if (p.accept(endPoint)){
                    processors.add(p);
                }
            }
            endPointAcceptCache.put(endPoint,processors);
        }
        for (MethodInterceptor p : processors){
            if (p.needExec(context,result)){

                result = doInterceptExec(p,context,result);
            }
        }

        return result;
    }

    @Override
    public boolean accept(Method method, Class<?> aClass) {
        EndPoint before = new EndPoint(aClass,method,EndPoint.BEFORE);
        EndPoint after = new EndPoint(aClass,method,EndPoint.AFTER);
        if (processorCache!=null) {
            for (MethodInterceptor i : this.processorCache) {
                if (i.accept(before) || i.accept(after))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (MethodInterceptor i: processorCache){
            i.setup(applicationContext);
        }

        logger.info("ContextRefreshedEvent");
    }
}
