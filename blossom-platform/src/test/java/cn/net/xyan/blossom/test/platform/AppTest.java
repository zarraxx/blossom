package cn.net.xyan.blossom.test.platform;

import cn.net.xyan.blossom.core.jpa.support.EasyJpaRepositoryFactoryBean;
import cn.net.xyan.blossom.platform.BlossomBootstrapConfiguration;
import cn.net.xyan.blossom.platform.BlossomConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by zarra on 16/5/13.
 */

public class AppTest {
    public static void main(String[] args) {
        //System.out.println(StatusAndType.StatusAndTypeIDFormat.class.getName());

        new SpringApplicationBuilder(BlossomBootstrapConfiguration.class)
                .child(BlossomConfiguration.class).web(true)
                .run(args);
    }

    @Configuration
    //@Import(BlossomConfiguration.class)
    static class AppConfig {

//        @Bean
//        @UIScope
//        public BSideBar bSideBar (SideBarUtils sideBarUtils){
//            return new BSideBar(sideBarUtils);
//        }

    }

}
