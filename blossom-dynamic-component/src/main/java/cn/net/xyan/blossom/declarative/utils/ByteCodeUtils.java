package cn.net.xyan.blossom.declarative.utils;


import cn.net.xyan.blossom.declarative.ui.RuntimeContext;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

import java.lang.annotation.Annotation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/9.
 */
public class ByteCodeUtils {

    public static final String SUPERMethodPrefix = "$SUPER$";
    public static final String RETURNTypeKey = "__return__type__";
    public static final String RuntimeContextFieldName = "__runtimeContext__";
    public static final String RuntimeProxyFieldName = "__runtimeProxy__";


    public static final String setterCodeFormat = "public void %s (%s x) { this.%s = x ; }";
    public static final String getterCodeFormat = "public %s %s () { return this.%s ; }";

    public static CtClass newClass(ClassMetaModel classMetaModel) throws Exception {

        ClassPool pool = ClassPool.getDefault();

        // Create the class.
        CtClass subClass = pool.makeClass(classMetaModel.getFullName());
        final CtClass superClass = pool.get(classMetaModel.getSuperClass().getName());
        subClass.setSuperclass(superClass);
        subClass.setModifiers(javassist.Modifier.PUBLIC);

        for (ClassMetaModel.ConstructorParams constructorParams : classMetaModel.getConstructorParams()){
            addConstructorToCls(subClass,constructorParams);
        }

        if (classMetaModel.getFields().size() > 0) {
            for (ClassMetaModel.DynamicFieldInfo fieldInfo : classMetaModel.getFields()) {
                addFieldToCls(subClass, fieldInfo);
            }

        }

        for (Class<? extends Annotation> ac : classMetaModel.getAnnotations()) {
            addAnnotationToCls( subClass,ac);
        }

        return subClass;


    }

    public static CtClass addConstructorToCls(CtClass ctClass, ClassMetaModel.ConstructorParams constructorParams) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        List<CtClass> paramCls = new LinkedList<>();
        CtConstructor ctor = null;

        for (Class<?> cls : constructorParams) {
            paramCls.add(pool.get(cls.getName()));
        }

        if (paramCls.size() > 0) {
            ctor = CtNewConstructor.make(paramCls.toArray(new CtClass[0]), null, CtNewConstructor.PASS_PARAMS, null, null, ctClass);
        } else
        {
            ctor = CtNewConstructor.make(null, null, CtNewConstructor.PASS_NONE, null, null, ctClass);
        }
        if (ctor!=null)
            ctClass.addConstructor(ctor);

        ctClass.addConstructor(ctor);

        return ctClass;

    }

    public static CtClass addFieldToCls(CtClass ctClass, ClassMetaModel.DynamicFieldInfo fieldInfo) throws Exception {
        ClassPool pool = ClassPool.getDefault();

        CtClass fieldCCls = pool.get(fieldInfo.getFieldType().getName());

        CtField f = new CtField(fieldCCls, fieldInfo.getFieldName(), ctClass);
        ctClass.addField(f);
        return ctClass;
    }

    public static CtClass addAnnotationToCls( CtClass clazz,Class<? extends Annotation> annotationCls) {

        ClassPool pool = ClassPool.getDefault();
        //CtClass clazz = pool.get(cls.getName());
        ClassFile cfile = clazz.getClassFile();

        ConstPool cpool = cfile.getConstPool();
        // CtField cfield  = clazz.getField(fieldName);

        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(annotationCls.getName(), cpool);
        attr.addAnnotation(annot);

        cfile.addAttribute(attr);

        return clazz;

    }

    public static CtMethod addMethod(String code,CtClass ctClass) throws CannotCompileException {
        System.out.println(code);
        CtMethod ctMethod=CtNewMethod.make(code, ctClass);

        return ctMethod;
    }

    public static CtClass addSetterMethod(String fieldName,String setterName,Class<?> setterType,CtClass ctClass) throws CannotCompileException {
        String typeName = setterType.getTypeName();
        String setterCode = String.format(setterCodeFormat,setterName,typeName,fieldName);
        ctClass.addMethod(addMethod(setterCode,ctClass));
        return ctClass;
    }

    public static CtClass addGetterMethod(String fieldName,String getterName,Class<?> getterType,CtClass ctClass) throws CannotCompileException {
        String typeName = getterType.getTypeName();
        String getterCode = String.format(getterCodeFormat,typeName,getterName,fieldName);
        ctClass.addMethod(addMethod(getterCode,ctClass));
        return ctClass;
    }

    public static boolean classHasInterface(CtClass ctClass,Class<?> interfaceCls) throws Exception{
        ClassPool pool = ClassPool.getDefault();

        CtClass ctInterfaceCls = pool.get(interfaceCls.getName());

        for (CtClass i :ctClass.getInterfaces() ){
            if (i.getName() == ctInterfaceCls.getName()){
                return true;
            }
        }
        return false;
    }

    public static CtClass addMethod(CtClass ctClass,MethodMetaModel methodMetaModel)throws Exception{

        ClassPool pool = ClassPool.getDefault();

       if (!classHasInterface(ctClass,DynamicMethodAvailable.class)){
           addStubInterfaceToCls(ctClass);
       }
        String code = methodMetaModel.generateMethodDeclare() + methodMetaModel.generateMethodBody();
        //System.out.println(code);
        CtMethod ctMethod = addMethod(code,ctClass);

       ctClass.addMethod(ctMethod);

        return ctClass;
    }

    public static CtClass addStubInterfaceToCls(CtClass ctClass) throws Exception {

        ClassPool pool = ClassPool.getDefault();

        CtClass interfaceCls = pool.get(DynamicMethodAvailable.class.getName());

        ctClass.addInterface(interfaceCls);

        ClassMetaModel.DynamicFieldInfo dynamicFieldInfo = new ClassMetaModel.DynamicFieldInfo(RuntimeContextFieldName, RuntimeContext.class);
        ClassMetaModel.DynamicFieldInfo proxyFieldInfo = new ClassMetaModel.DynamicFieldInfo(RuntimeProxyFieldName, DynamicMethodProxy.class);

        addFieldToCls(ctClass, dynamicFieldInfo);
        addFieldToCls(ctClass, proxyFieldInfo);


        addGetterMethod(RuntimeContextFieldName,DynamicMethodAvailable.CONTEXTGETTER,RuntimeContext.class,ctClass);
        addSetterMethod(RuntimeContextFieldName,DynamicMethodAvailable.CONTEXTSETTER,RuntimeContext.class,ctClass);

        addGetterMethod(RuntimeProxyFieldName,DynamicMethodAvailable.PROXYGETTER,DynamicMethodProxy.class,ctClass);
        addSetterMethod(RuntimeProxyFieldName,DynamicMethodAvailable.PROXYSETTER,DynamicMethodProxy.class,ctClass);

        return ctClass;

    }


}
