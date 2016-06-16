package cn.net.xyan.blossom.core.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by zarra on 16/6/7.
 */
public class JsonUtils {


    static public JavaType mapType( Class<?> valueCls){
        return objectMapper().getTypeFactory().constructMapType(Map.class,String.class,valueCls);
    }

    static public JavaType mapType(Class<?> keyCls, Class<?> valueCls){
        return objectMapper().getTypeFactory().constructMapType(Map.class,keyCls,valueCls);
    }

    static public JavaType listType(Class<?> cls){
        return objectMapper().getTypeFactory().constructCollectionType(List.class,cls);
    }

    static public JavaType listType(JavaType javaType){
        return objectMapper().getTypeFactory().constructCollectionType(List.class,javaType);
    }

    public static ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
