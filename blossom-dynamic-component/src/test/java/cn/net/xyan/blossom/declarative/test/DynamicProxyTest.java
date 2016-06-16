package cn.net.xyan.blossom.declarative.test;


import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.declarative.utils.*;

import javassist.CtClass;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by zarra on 16/6/9.
 */
public class DynamicProxyTest {

    public static String StringReturnValue = "hello world from proxy!!!!!!!!";
    public static Integer IntReturnValue = 9999;

    public static class SimpleDynamicMethodProxy implements DynamicMethodProxy {



        @Override
        public Object invoke(DynamicMethodAvailable from, String methodName, Object[][] params) {
            System.out.println("-------Dynamic Method -----------");
            System.out.println("you call:" + methodName);
            System.out.println("from:" + from.toString());

            Object returnValue = null;
            try {
                if (params != null) {
                    Object[] returnInfo = params[0];
                    String returnTypeName = (String)returnInfo[1];
                    Class<?> returnType = Class.forName(returnTypeName);

                    for (int i=1;i<params.length;i++){
                        String key = (String) params[i][0];
                        System.out.print("key:" + key);
                        Object v = params[i][1];
                        if (v == null)
                            System.out.println("\tvalue:NULL");
                        else
                            System.out.println("\tvalue:" + v.toString());
                    }

                    if (String.class.isAssignableFrom(returnType)) {
                        return StringReturnValue;
                    } else if (Integer.class.isAssignableFrom(returnType)) {
                        return IntReturnValue;
                    }
                }
            } catch (Throwable e) {

            }

            System.out.println("-------Dynamic Method End-----------");

            return returnValue;
        }
    }

    @Test
    public void doTest() throws Throwable {
        String newClassName = "a.b.c.de.f.e.d";
        ClassMetaModel classMetaModel = new ClassMetaModel(newClassName, ClassA.class);

        CtClass ctClass = ByteCodeUtils.newClass(classMetaModel);

        ctClass = ByteCodeUtils.addStubInterfaceToCls(ctClass);


        MethodMetaModel methodMetaModel = new MethodMetaModel("add", Integer.class);

        methodMetaModel.addParam("a", Integer.class);
        methodMetaModel.addParam("b", Integer.class);
        ByteCodeUtils.addMethod( ctClass, methodMetaModel);

        //------------------------------------------------------
        MethodMetaModel methodMetaModelHello = new MethodMetaModel("hello", String.class);//the same as super class

        ByteCodeUtils.addMethod(ctClass, methodMetaModelHello);

        //------------------------------------------------------
        MethodMetaModel methodMetaModelVoid = new MethodMetaModel("voidFunc", Void.class);//the same as super class

        ByteCodeUtils.addMethod(ctClass, methodMetaModelVoid);

        //------------------------------------------------------
        MethodMetaModel methodMetaModelVoid2 = new MethodMetaModel("voidFunc", Void.class);//the same as super class
        methodMetaModelVoid2.addParam("a", String.class);
        methodMetaModelVoid2.getThrowableCls().add(Exception.class);
        ByteCodeUtils.addMethod(ctClass, methodMetaModelVoid2);

        //------------------------------------------------------

        Method superMethod  = ClassA.class.getMethod("voidFunc",String.class);
        Method superMethod2 = ClassA.class.getMethod("add",Integer.class,Integer.class);
        SuperMethodWarpMetaModel superMethodWarpInfo = new SuperMethodWarpMetaModel(superMethod);
        SuperMethodWarpMetaModel superMethodWarpInfo2 = new SuperMethodWarpMetaModel(superMethod2);

        ByteCodeUtils.addMethod(ctClass,superMethodWarpInfo);
        ByteCodeUtils.addMethod( ctClass,superMethodWarpInfo2);


        MethodMetaModel newMethodMetaModel = new MethodMetaModel("newAdd", Integer.class);

        newMethodMetaModel.addParam("a", Integer.class);
        newMethodMetaModel.addParam("b", Integer.class);
        ByteCodeUtils.addMethod( ctClass, newMethodMetaModel);


        ctClass.debugWriteFile("target");

        Class<? extends ClassA> cls = ctClass.toClass();

        ClassA a = cls.newInstance();


        Method[] methods = a.getClass().getDeclaredMethods();

        for (Method method : methods) {
            System.out.println(method.getName());
        }

        if (a instanceof DynamicMethodAvailable) {
            DynamicMethodAvailable stub = (DynamicMethodAvailable) a;

            RuntimeContext runtimeContext = new RuntimeContext();

            String key = "key";

            String value = "value";

            runtimeContext.put(key, value);

            stub.DynamicMethodAvailableSetRuntimeContext(runtimeContext);

            runtimeContext = stub.DynamicMethodAvailableGetRuntimeContext();

            Assert.assertEquals(value, runtimeContext.get(key));

            SimpleDynamicMethodProxy dynamicMethodProxy = new SimpleDynamicMethodProxy();

            stub.DynamicMethodAvailableSetProxy(dynamicMethodProxy);

            System.out.println(a.hello());

            System.out.println(a.add(1,null));

            a.voidFunc();

            a.voidFunc("ccc");

            Assert.assertEquals(a.hello(),StringReturnValue);

            Assert.assertEquals(a.add(1,2),IntReturnValue);

            //-----call super method

            Method superM = cls.getMethod(ByteCodeUtils.SUPERMethodPrefix+"add",Integer.class,Integer.class);

            Integer superV = (Integer) superM.invoke(a,1,2);

            Assert.assertEquals(new Integer(1+2),superV);
        }



    }
}
