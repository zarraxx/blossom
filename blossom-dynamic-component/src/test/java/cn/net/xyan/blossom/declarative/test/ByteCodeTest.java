package cn.net.xyan.blossom.declarative.test;

import cn.net.xyan.blossom.declarative.utils.ByteCodeUtils;
import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by zarra on 16/6/9.
 */
public class ByteCodeTest {
    @Test
    public void doTest() throws Exception {

        String newClassA = "a.a.a.a.a.a.a";

        String newClassB = "a.a.a.a.a.a.a2";

        ClassMetaModel n1 = new ClassMetaModel(newClassA, ClassA.class);

        ClassMetaModel n2 = new ClassMetaModel(newClassB, ClassB.class);

        n2.getFields().add(new ClassMetaModel.DynamicFieldInfo("f1", Integer.class));
        n2.getFields().add(new ClassMetaModel.DynamicFieldInfo("f2", Date.class));

        n2.getAnnotations().add(Deprecated.class);

        Class<?> nc1 = ByteCodeUtils.newClass(n1).toClass();

        Assert.assertEquals(newClassA,nc1.getName());

        Class<?> nc2 = ByteCodeUtils.newClass(n2).toClass();

//        for (Constructor constructor : nc2.getConstructors()) {
//            System.out.println(constructor.getName());
//        }

        Assert.assertEquals(newClassB,nc2.getName());

        Field[] fields = nc2.getDeclaredFields();

        Assert.assertEquals(2,fields.length);


        Deprecated deprecated = nc2.getAnnotation(Deprecated.class);

        org.junit.Assert.assertNotNull(deprecated);


        ClassA classA = (ClassA) nc1.newInstance();

        Assert.assertNotNull(classA);

        Constructor<ClassB> classBConstructor = (Constructor<ClassB>) nc2.getConstructor(String.class,Date.class);

        ClassB classB = classBConstructor.newInstance(null,null);

        Assert.assertNotNull(classB);
    }
}
