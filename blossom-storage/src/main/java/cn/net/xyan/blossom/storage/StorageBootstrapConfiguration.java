package cn.net.xyan.blossom.storage;

import cn.net.xyan.blossom.core.jpa.support.EasyJpaRepositoryFactoryBean;
import cn.net.xyan.blossom.platform.annotation.BootstrapConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Created by zarra on 2016/10/19.
 */
@Configuration
@BootstrapConfiguration
@EntityScan(
        basePackages = {
                "cn.net.xyan.blossom.storage.entity"
        })
@EnableJpaRepositories(
        basePackages = {
                "cn.net.xyan.blossom.storage.dao"
        },
        repositoryFactoryBeanClass = EasyJpaRepositoryFactoryBean.class
)
public class StorageBootstrapConfiguration {
}
