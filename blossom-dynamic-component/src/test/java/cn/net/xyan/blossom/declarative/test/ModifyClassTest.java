package cn.net.xyan.blossom.declarative.test;

import cn.net.xyan.blossom.declarative.utils.*;
import javassist.CtClass;
import javassist.Loader;
import org.junit.Test;
import org.springframework.test.annotation.TestAnnotationUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/10.
 */
public class ModifyClassTest {

    public void print(String s){
        System.out.println(s);
    }


    public void printObj(Object a){
        print("----------------------");
        print(a.toString());
        for (Method method : a.getClass().getMethods()){
            print(method.getName());
        }
        print("----------------------");
    }

    @Test
    public void doTest() throws Exception {
        Loader cl = new Loader(ByteCodeUtils.classPool());


        ClassMetaModel metaModel = new ClassMetaModel("target.testPackage.test.a.TestForClassA",ClassA.class);
        List<MethodMetaModel> methodMetaModels = new LinkedList<>();

        DynamicProxyTest.SimpleDynamicMethodProxy simpleDynamicMethodProxy = new DynamicProxyTest.SimpleDynamicMethodProxy();

        CtClass ctClass = ByteCodeUtils.newClass(metaModel);

        ByteCodeUtils.addStubInterfaceToCls(ctClass);


        //cl.loadClass(ClassA.class.getName());
       Class TestForClassA  = cl.loadClass(ctClass.getName());
//
        Object a =  TestForClassA.newInstance();



        if (a instanceof DynamicMethodAvailable){
            DynamicMethodAvailable dynamicMethodAvailable = (DynamicMethodAvailable) a;
        }
//
        printObj(a);

        Class DynamicMethodProxyClass = cl.loadClass("cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy");

        Method method = a.getClass().getMethod("DynamicMethodAvailableSetProxy", DynamicMethodProxyClass);

//
        a= null;
        TestForClassA =null;
        System.gc();

        //metaModel.getFields().add(new ClassMetaModel.DynamicFieldInfo("newField1",String.class));

        methodMetaModels.add(new MethodMetaModel("add2",Integer.class));

       // for (MethodMetaModel methodMetaModel: methodMetaModels){
       //     ctClass = ByteCodeUtils.addMethod(ctClass,methodMetaModel);
      //  }

        ctClass = ByteCodeUtils.modifyClass(/*ctClass,*/metaModel,methodMetaModels);

       // ctClass = ByteCodeUtils.newClass(metaModel);

        ctClass.debugWriteFile();

        if (ctClass !=null){


            cl = new Loader(ByteCodeUtils.classPool());
             Class TestForClassANew  = cl.loadClass(ctClass.getName());

             //TestForClassA = ctClass.toClass();
             Object aNew = TestForClassANew.newInstance();

            printObj(aNew);
        }

       // TestForClassA  = ctClass.toClass();
    }
}
