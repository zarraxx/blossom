package cn.net.xyan.blossom.platform;

import cn.net.xyan.blossom.core.jpa.support.EasyJpaRepositoryFactoryBean;
import cn.net.xyan.blossom.platform.annotation.BootstrapConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by zarra on 16/8/20.
 */
@Configuration
@BootstrapConfiguration
@Import({DataSourceAutoConfiguration.class
        ,HibernateJpaAutoConfiguration.class
        //,EmbeddedServletContainerAutoConfiguration.class
})
@EntityScan(
        basePackages = {
                "cn.net.xyan.blossom.core.jpa.entity"
                ,"cn.net.xyan.blossom.declarative.entity"
                ,"cn.net.xyan.blossom.platform.entity"
        })
@EnableJpaRepositories(
        basePackages = {
                "cn.net.xyan.blossom.core.jpa.dao"
                ,"cn.net.xyan.blossom.declarative.dao"
                ,"cn.net.xyan.blossom.platform.dao"
        },
        repositoryFactoryBeanClass = EasyJpaRepositoryFactoryBean.class
)
@EnableTransactionManagement
public class BlossomBootstrapConfiguration {
}
