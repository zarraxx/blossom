package cn.net.xyan.blossom.core.support;

import cn.net.xyan.blossom.core.annotation.EnableBootstrap;
import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by zarra on 2017/5/5.
 */
public class BootstrapRegistrar implements ImportBeanDefinitionRegistrar {

    String filePath;

    Logger logger = LoggerFactory.getLogger(BootstrapRegistrar.class);

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static BeanDefinitionBuilder beanDefinitionBuilder(Class<?> beanCls){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanCls).setLazyInit(false);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        return builder;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> values = annotationMetadata.getAnnotationAttributes(EnableBootstrap.class.getName());
        String value = "classpath*:/META-INF/bootstrap.properties";
        if (values.size()>0 && values.containsKey("value")){
            String v = (String) values.get("value");
            if(!StringUtils.isEmpty(v))
                value = v;
        }
        setFilePath(value);

        PathMatchingResourcePatternResolver     resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources(getFilePath());
            for (Resource resource:resources){
                try (InputStream inputStream = resource.getInputStream()){
                    String content = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
                    String[] lines = content.split("\n");
                    for (String line : lines){
                        try {
                            line = line.trim();
                            if (line.length()== 0)
                                continue;
                            BeanDefinitionBuilder builder = beanDefinitionBuilder(Class.forName(line));
                            registry.registerBeanDefinition(line,builder.getBeanDefinition());
                        } catch (ClassNotFoundException e) {
                            logger.error(ExceptionUtils.errorString(e));
                            continue;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new StatusAndMessageError(-9,e);
        }
    }
}
