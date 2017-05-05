package cn.net.xyan.blossom.core.test;

import cn.net.xyan.blossom.core.annotation.EnableBootstrap;
import cn.net.xyan.blossom.core.jpa.support.EasyJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by zarra on 16/4/23.
 */
@SpringBootApplication
public class TESTApplication {
    public static void main(String[] args) {
        SpringApplication.run(TESTApplication.class, args);
    }

    @EntityScan(basePackages = {"cn.net.xyan.blossom.core.test.entity"})
    @EnableJpaRepositories(
            basePackages = {"cn.net.xyan.blossom.core.test.dao"},
            repositoryFactoryBeanClass = EasyJpaRepositoryFactoryBean.class
    )
    //@EnableBootstrap
    static public class TESTApplicationConfig{

    }
}
