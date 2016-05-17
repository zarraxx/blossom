package cn.net.xyan.blossom.core.utils;

/**
 * Created by zarra on 16/4/23.
 */

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiashenpin on 16/1/19.
 */
public class ReflectUtils {
    static public List<Field> fields(Class<?> cls) {
        List<Field> fields = new LinkedList<>();
        for (Field field : cls.getDeclaredFields()) {
            fields.add(field);
        }
        return fields;
    }

    static public List<Field> fields(Class<?> cls, Class<? extends Annotation>... annotationCls) {
        List<Field> fields = new LinkedList<>();
        for (Field field : cls.getDeclaredFields()) {

            boolean add = true;
            for (Class<? extends Annotation> annotationClass : annotationCls) {
                if (!field.isAnnotationPresent(annotationClass)) {
                    add = false;
                    continue;
                }
            }

            if (add)
                fields.add(field);
        }
        return fields;
    }

    static public List<Method> methods(Class<?> cls) {
        List<Method> methods = new LinkedList<>();
        for (Method method : cls.getDeclaredMethods()) {
            methods.add(method);
        }
        return methods;
    }

    static public List<Method> methods(Class<?> cls, Class<? extends Annotation>... annotationCls) {
        List<Method> methods = new LinkedList<>();
        for (Method method : cls.getMethods()) {
            boolean add = true;
            for (Class<? extends Annotation> annotationClass : annotationCls) {
                if (!method.isAnnotationPresent(annotationClass)) {
                    add = false;
                    continue;
                }
            }
            if (add)
                methods.add(method);

        }
        return methods;
    }

    public static String getPropertyName(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                    //System.out.println(pd.getDisplayName());
                    return pd.getName();
                }
            }

            return null;
        } catch (Exception e) {
            throw new StatusAndMessageError(-99, e);
        }

    }

    public static Object getProperty(Object obj, PropertyDescriptor propertyDescriptorse) {
        try {
            return BeanUtils.getProperty(obj, propertyDescriptorse.getName());
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> propertyNameWithAnnotation(Class<?> beanCls,Class<? extends Annotation>... annotationCls){
        List<Method> methods = methods(beanCls,annotationCls);
        List<String> result = new LinkedList<>();
        for (Method method:methods){
            result.add(getPropertyName(method));
        }
        if (result.size()==0){
            for (Field field:fields(beanCls,annotationCls)){
                result.add(field.getName());
            }
        }
        return result;
    }

    public static Object staticVariableValue(Class<?> c,String fieldName){

        Object result  = null;
        try {
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            if(f.isAccessible()){
                result = f.get(null);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result ;

    }

    public static <T> T newInstance(Class<T> type) throws Exception {

        Constructor<T> constructor = type.getConstructor();
        return constructor.newInstance();

    }
}

