package cn.net.xyan.blossom.core.jpa.annotation;


import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.jpa.utils.query.DefaultPredicateCreator;
import cn.net.xyan.blossom.core.jpa.utils.query.PredicateCreator;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zarra on 16/1/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface QueryCondition {

    String value() default ""; //实体类字段名称
    String[] values() default {};//需要连接的字段名称数组

    JPA.Operator operator() default JPA.Operator.Equal;
    boolean ignoreNull() default true;//字段为null 时是否忽略此条件

    boolean hasIgnoreValue() default false;
    String ignoreValue() default "";//字段为该值时 忽略此条件

    Class<? extends PredicateCreator> creator() default DefaultPredicateCreator.class; //条件生成器

    boolean join() default false;
    JoinType joinType() default JoinType.INNER; //连接类型
}
