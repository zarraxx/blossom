package cn.net.xyan.blossom.core.utils;

/**
 * Created by zarra on 16/4/23.
 */

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.method.P;

import java.beans.BeanInfo;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

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

    public static PropertyDescriptor[] getPropertyDescriptors(Object obj){
        return BeanUtils.getPropertyDescriptors(obj.getClass());
    }

    public static PropertyDescriptor getPropertyDescriptor(Object obj,String propertyName){
        return BeanUtils.getPropertyDescriptor(obj.getClass(),propertyName);
    }

    public static Object getProperty(Object obj, PropertyDescriptor propertyDescriptorse) {
        try {
            Method readMethod =propertyDescriptorse.getReadMethod();

            if(!readMethod.isAccessible()) {
                readMethod.setAccessible(true);
            }
            return readMethod.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getSimpleProperty(Object obj,String propertyName){
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj,propertyName);
        return getProperty(obj,propertyDescriptor);
    }

    public static Object getProperty(Object obj,List<String> propertyNames){
        if (propertyNames.size() > 0){
            Object p =  getSimpleProperty(obj,propertyNames.get(0));

            if (propertyNames.size() > 1){
                int size = propertyNames.size();
                List<String> newPropertyNames = propertyNames.subList(1,size);
                return getProperty(p,newPropertyNames);
            }else{
                return p;
            }
        }

        return null;
    }

    public static Object getProperty(Object obj,String propertyName){
        String[] propertyNames = propertyName.split("\\.");
        List<String> list = Arrays.asList(propertyNames);
        return getProperty(obj,list);
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

    public static  <T> Set< Class<? extends T> > scanPackages(Class<T> tClass,String ... packages){
        Reflections reflections = new Reflections(packages);

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(tClass);
        Set<Class<? extends T>> result = new HashSet<>();
        for (Class<? extends T> cls : classes) {
            if (!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())) {
                result.add(cls);
            }
        }
        return result;
    }
}

