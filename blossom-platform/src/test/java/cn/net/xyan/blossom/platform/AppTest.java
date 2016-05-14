package cn.net.xyan.blossom.platform;

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
        SpringApplication.run(AppTest.class, args);
    }

    @Configuration
    @Import(BlossomConfiguration.class)
    static class AppConfig{

    }

}
