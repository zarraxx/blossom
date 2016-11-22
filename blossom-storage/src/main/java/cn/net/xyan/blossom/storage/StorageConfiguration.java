package cn.net.xyan.blossom.storage;

import cn.net.xyan.blossom.platform.annotation.BootstrapConfiguration;
import cn.net.xyan.blossom.storage.service.FileSystemService;
import cn.net.xyan.blossom.storage.service.StorageService;
import cn.net.xyan.blossom.storage.service.impl.FileSystemServiceImpl;
import cn.net.xyan.blossom.storage.service.impl.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Created by zarra on 2016/10/19.
 */
@Configuration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(value=BootstrapConfiguration.class,type= FilterType.ANNOTATION)})
public class StorageConfiguration {

    @Bean
    public StorageService storageService(){
        return new StorageServiceImpl();
    }

    @Bean
    public FileSystemService fileSystemService(){
        return new FileSystemServiceImpl();
    }
}
