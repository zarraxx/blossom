package cn.net.xyan.blossom.core.jpa.annotation;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiashenpin on 16/1/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ResultColumn {

    String value() default ""; //实体类字段名称
    String[] values() default {};//需要连接的字段名称数组

    boolean join() default false;
    JoinType joinType() default JoinType.INNER; //连接类型
}
