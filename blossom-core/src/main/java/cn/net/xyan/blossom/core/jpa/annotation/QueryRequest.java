package cn.net.xyan.blossom.core.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zarra on 16/1/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface QueryRequest {
    Class<?> entityType();
    boolean distinct() default  false;
}
