package cn.net.xyan.blossom.platform;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


/**
 * Created by zarra on 16/5/13.
 */
@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class)
public class AppTest {
    public static void main(String[] args) {
        //System.out.println(StatusAndType.StatusAndTypeIDFormat.class.getName());
        SpringApplication.run(AppTest.class, args);
    }

    @Configuration
    //@Import(BlossomConfiguration.class)
    static class AppConfig{

//        @Bean
//        @UIScope
//        public BSideBar bSideBar (SideBarUtils sideBarUtils){
//            return new BSideBar(sideBarUtils);
//        }

    }

}
