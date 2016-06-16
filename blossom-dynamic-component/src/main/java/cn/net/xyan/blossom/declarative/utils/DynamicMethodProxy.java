package cn.net.xyan.blossom.declarative.utils;

import java.util.Map;

/**
 * Created by zarra on 16/6/9.
 */
public interface DynamicMethodProxy {
    //Object invoke(DynamicMethodAvailable from,String methodName,Object ... params);
    Object invoke(DynamicMethodAvailable from,String methodName,Object[][] params);
}
