package cn.net.xyan.blossom.platform.intercept.impl;
import cn.net.xyan.blossom.platform.intercept.InterceptService;
import cn.net.xyan.blossom.platform.intercept.annotation.ProcessParam;
import cn.net.xyan.blossom.platform.intercept.model.EndPoint;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class NeedMoreProcessAdvice implements MethodInterceptor {

    InterceptService interceptService;

    Logger logger = LoggerFactory.getLogger(NeedMoreProcessAdvice.class);

    public NeedMoreProcessAdvice(){}

    public InterceptService getInterceptService() {
        return interceptService;
    }

    public void setInterceptService(InterceptService interceptService) {
        this.interceptService = interceptService;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object target = methodInvocation.getThis();
        Method method = methodInvocation.getMethod();
        Object[] params = methodInvocation.getArguments();
        Object result;

        Map<String,Object> context = createContext(method,params);

        context.put(InterceptService.KEYTarget,target);
        context.put(InterceptService.KEYMethod,method);

        EndPoint before = new EndPoint(target.getClass(),method,EndPoint.BEFORE);

        getInterceptService().doIntercept(before,context,null);

        try{

            result = methodInvocation.proceed();

            //System.out.println("Around method : after ");
            //return  result;

        }catch(Throwable e){
            logger.error("Around method : throw  an  exception ");
            EndPoint exception = new EndPoint(target.getClass(),method,EndPoint.EXCEPTION);
            getInterceptService().doIntercept(exception,context,e);

            throw  e;
        }

        EndPoint after = new EndPoint(target.getClass(),method,EndPoint.AFTER);

        return getInterceptService().doIntercept(after,context,result);
    }

    static public String varNameForClass(Class<?> cls){
        String name = cls.getSimpleName();

        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        return name;
    }

    public static String findObjectName(Annotation[] annotations, Class<?> cls, String classNameIndex) {
        String objName = varNameForClass(cls);

        if (classNameIndex != null)
            objName = objName + classNameIndex;

        //char ch = Character.toLowerCase(objName.charAt(0));

        for (Annotation an : annotations) {
            if (an.annotationType().equals(ProcessParam.class)) {
                ProcessParam processParam = (ProcessParam) an;
                objName = processParam.value();
                break;
            }
        }

        return objName;

    }

    public static Map<String, Object> createContext(Method method, Object[] args) {
        Map<String, Object> context = new HashMap<>();
        Map<Class<?>, Integer> classCountMap = new HashMap<>();
        Map<Class<?>, Integer> classIndexMap = new HashMap<>();

        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final Class[] classes = method.getParameterTypes();

        for (Class<?> cls : method.getParameterTypes()) {
            Integer count = classCountMap.get(cls);
            if (count == null) {
                count = 0;
            }
            count++;
            classCountMap.put(cls, count);
        }


        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            Class<?> declareCls = classes[i];
            Integer count = classCountMap.get(declareCls);
            String nameIndex = null;
            if (count > 1) {
                Integer index = classIndexMap.get(declareCls);
                if (index == null) {
                    index = 0;
                }
                index++;
                classIndexMap.put(declareCls, index);
                nameIndex = String.valueOf(index);
            }

            String objName = findObjectName(paramAnnotations[i], declareCls, nameIndex);
            context.put(objName, obj);
        }

        return context;
    }

}
