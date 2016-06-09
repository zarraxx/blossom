package cn.net.xyan.blossom.declarative.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by zarra on 16/6/9.
 */
public class SuperMethodWarpMetaModel extends MethodMetaModel {
    Method method ;
    String superMethodName ;
    public SuperMethodWarpMetaModel(Method method){
        super(ByteCodeUtils.SUPERMethodPrefix+method.getName(),method.getReturnType());
        Class<?> rt = method.getReturnType();
        this.method = method;
        superMethodName = method.getName();
        for (Parameter parameter: method.getParameters()){
            addParam(parameter.getName(),parameter.getType());
        }

        for (Class<?> tc : method.getExceptionTypes()){
            getThrowableCls().add((Class<? extends Throwable>) tc);
        }

    }

    @Override
    public String generateMethodBody() {
        String returnString = "";

        if (!getReturnType().equals(Void.TYPE)){
            returnString = "return ";
        }
        String paramList = MethodMetaModel.join(getParamNames(),",");
        String format = "{ %s super.%s(%s);}";

        return String.format(format,returnString,superMethodName,paramList);
    }
}
