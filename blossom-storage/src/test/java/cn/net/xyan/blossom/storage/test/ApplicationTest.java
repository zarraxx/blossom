package cn.net.xyan.blossom.storage.test;

import cn.net.xyan.blossom.platform.BlossomBootstrapConfiguration;
import cn.net.xyan.blossom.platform.BlossomConfiguration;
import cn.net.xyan.blossom.storage.StorageBootstrapConfiguration;
import cn.net.xyan.blossom.storage.StorageConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by zarra on 2016/10/19.
 */
public class ApplicationTest {

    @cn.net.xyan.blossom.platform.annotation.BootstrapConfiguration
    @Configuration
    @Import({BlossomBootstrapConfiguration.class, StorageBootstrapConfiguration.class})
    public static class BootstrapConfiguration{

    }

    @Configuration
    @Import({BlossomConfiguration.class, StorageConfiguration.class})
    public static class AppConfiguration{

    }

    static public SpringApplicationBuilder applicationBuilder(){
        return  new SpringApplicationBuilder(BootstrapConfiguration.class)
                .child(AppConfiguration.class);
    }

    public static void main(String[] args) {
        applicationBuilder().web(true).run(args);
    }


}
