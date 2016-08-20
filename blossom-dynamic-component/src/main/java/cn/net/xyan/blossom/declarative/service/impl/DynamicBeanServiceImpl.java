package cn.net.xyan.blossom.declarative.service.impl;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.declarative.dao.BeanDefinitionDao;
import cn.net.xyan.blossom.declarative.entity.DynamicBeanDefinition;
import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.declarative.script.ScriptFactoryBean;
import cn.net.xyan.blossom.declarative.service.DynamicBeanService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zarra on 16/8/20.
 */
public class DynamicBeanServiceImpl implements DynamicBeanService{

    ApplicationContext applicationContext;

    ApplicationContext parentContext;

    BeanDefinitionDao beanDefinitionDao;

    @Override
    public void refresh() {

    }

    public BeanDefinitionBuilder beanDefinitionBuilder(DynamicBeanDefinition dbf){

        try {
            Class<?> beanType = Class.forName(dbf.getBeanInterfaceName());
            InputStream in = new ByteArrayInputStream(dbf.getAttach().getBytes("UTF-8"));
            RuntimeContext context = new RuntimeContext();
            boolean singleton = false == dbf.getSingleton()?false:true;

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ScriptFactoryBean.class).setLazyInit(true);

            builder.addPropertyValue("singleton",singleton);
            builder.addPropertyValue("interfaceCls",beanType);
            builder.addPropertyValue("in",in);
            builder.addPropertyValue("runtimeContext",context);

            return builder;

        }catch (Throwable e){
            throw  new StatusAndMessageError(-9,e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        parentContext = this.applicationContext.getParent();
        if (parentContext!=null){
            beanDefinitionDao = parentContext.getBean(BeanDefinitionDao.class);
        }
    }



    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        List<DynamicBeanDefinition> beanDefinitions = beanDefinitionDao.findAll();


    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent){

        }else if(event instanceof ContextRefreshedEvent){

        }
    }
}
