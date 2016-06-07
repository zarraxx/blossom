package cn.net.xyan.blossom.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by zarra on 16/6/7.
 */
public class JsonUtils {

    public static ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
