package cn.net.xyan.blossom.core.annotation;

import cn.net.xyan.blossom.core.support.BootstrapRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by zarra on 2017/5/5.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(BootstrapRegistrar.class)
public @interface EnableBootstrap {
    String value() default "";
}
