package cn.net.xyan.blossom.platform;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by zarra on 16/5/13.
 */
@SpringBootApplication
public class AppTest {
    public static void main(String[] args) {
        //////System.out.println(StatusAndType.StatusAndTypeIDFormat.class.getName());
        SpringApplication.run(AppTest.class, args);
    }

    @Configuration
    @Import(BlossomConfiguration.class)
    static class AppConfig{

    }

}
