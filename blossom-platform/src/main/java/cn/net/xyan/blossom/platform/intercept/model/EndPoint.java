package cn.net.xyan.blossom.platform.intercept.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/8/19.
 */
public class EndPoint {

    public static final int BEFORE = 1;
    public static final int AFTER = 1;
    String className;
    String methodName;

    Class<?> targetClass;
    Method method;

    List<Class<?>> paramTypes = new LinkedList<>();
    int type;

    public EndPoint(Class<?> targetClass,Method method,int type){
        List<Class<?>> types = Arrays.asList(method.getParameterTypes());
        setParamTypes(types);
        setTargetClass(targetClass);
        setType(type);
        setMethod(method);
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Class<?>> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List<Class<?>> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        if (method!=null)
            setMethodName(method.getName());
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        if (targetClass!=null)
            setClassName(targetClass.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndPoint endPoint = (EndPoint) o;

        if (type != endPoint.type) return false;
        if (!className.equals(endPoint.className)) return false;
        if (!methodName.equals(endPoint.methodName)) return false;
        return paramTypes.equals(endPoint.paramTypes);

    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + paramTypes.hashCode();
        result = 31 * result + type;
        return result;
    }
}
