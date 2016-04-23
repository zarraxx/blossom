package cn.net.xyan.blossom.core.jpa.annotation;


import cn.net.xyan.blossom.core.jpa.utils.sequence.DoNothingSequenceFormat;
import cn.net.xyan.blossom.core.jpa.utils.sequence.ISequenceFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiashenpin on 16/1/19.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Sequence
{
    String value();
    long start() default 0L;
    long step() default 1L;
    long incrementValue() default 1L;
    Class<? extends ISequenceFormat> format() default DoNothingSequenceFormat.class;
}
