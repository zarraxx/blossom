package cn.net.xyan.blossom.declarative.utils;


import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.annotation.Annotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/9.
 */
public class ByteCodeUtils {

    public static  boolean isDebug = true;

    public static final String SUPERMethodPrefix = "$SUPER$";
    public static final String RETURNTypeKey = "__return__type__";
    public static final String RuntimeContextFieldName = "__runtimeContext__";
    public static final String RuntimeProxyFieldName = "__runtimeProxy__";


    public static final String setterCodeFormat = "public void %s (%s x) { this.%s = x ; }";
    public static final String getterCodeFormat = "public %s %s () { return this.%s ; }";

    public static ClassPool classPool(){
        return ClassPool.getDefault();
    }

    public static boolean isVoid(Class<?> cls){

        return cls.equals(Void.TYPE) || Void.class == cls;
    }

    public static CtClass newClass(ClassMetaModel classMetaModel) throws Exception {

        ClassPool pool = classPool();

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
        ClassPool pool = classPool();
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
        ClassPool pool = classPool();

        CtClass fieldCCls = pool.get(fieldInfo.getFieldType().getName());

        CtField f = new CtField(fieldCCls, fieldInfo.getFieldName(), ctClass);
        ctClass.addField(f);
        return ctClass;
    }

    public static CtClass addAnnotationToCls( CtClass clazz,Class<? extends Annotation> annotationCls) {

        ClassPool pool = classPool();
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
        if (isDebug)
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
        ClassPool pool = classPool();

        CtClass ctInterfaceCls = pool.get(interfaceCls.getName());

        for (CtClass i :ctClass.getInterfaces() ){
            if (i.getName() == ctInterfaceCls.getName()){
                return true;
            }
        }
        return false;
    }

    public static CtClass addMethod(CtClass ctClass,MethodMetaModel methodMetaModel)throws Exception{

        //ClassPool pool = ClassPool.getDefault();

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

        ClassPool pool = classPool();

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

    public static CtField findCtField(CtField[] fields,String name){
        for (CtField field : fields){
            if (field.getName() == name){
                return field;
            }
        }

        return null;
    }

    public static CtMethod findCtMethod(CtMethod[] methods,String name){
        for (CtMethod method : methods){
            if (method.getName() == name){
                return method;
            }
        }
        return null;
    }

    public static boolean isSameMethodDeclare(CtMethod method,MethodMetaModel methodMetaModel) throws NotFoundException {

        //must same name
        if (!method.getName().equals(methodMetaModel.getMethodName())){
            return false;
        }

        //must same return type
        CtClass returnType = method.getReturnType();
        Class<?> newReturnType = methodMetaModel.getReturnType();
        if (returnType.getName().equals("void")){
            if (!newReturnType.equals(Void.TYPE) && Void.class != newReturnType){
                return false;
            }
        }else {
            if (!returnType.getName().equals(methodMetaModel.getReturnType().getName())){
                return false;
            }
        }

        CtClass[] ctParamTypes =  method.getParameterTypes();

        //must same param length
        if (ctParamTypes.length!= methodMetaModel.getParamClass().size()){
            return false;
        }

        for (int i=0;i<ctParamTypes.length;i++){
            CtClass ctParamType = ctParamTypes[i];
            Class<?> newParamType = methodMetaModel.getParamClass().get(i);

            //all param type must the equal
            if (!ctParamType.getName().equals(newParamType.getName())){
                return false;
            }
        }

        return  true;
    }

    public static CtClass modifyClass(/*CtClass ctClass,*/ClassMetaModel classMetaModel,List<MethodMetaModel> methodMetaModels) throws Exception{
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String timestamp = df.format(now);
        String random = RandomStringUtils.random(8,false,true);

        String postfix = timestamp+random;

        ClassPool pool = classPool();
        CtClass ctClass = pool.get(classMetaModel.getFullName());

        boolean dirty = false;

        //defrost the class
        if(ctClass.isFrozen())
            ctClass.defrost();

        final CtClass superClass = pool.get(classMetaModel.getSuperClass().getName());

        CtClass ctSuperClass = ctClass.getSuperclass();
        if (ctSuperClass.getName()!= superClass.getName()){
            throw new UnsupportedOperationException("Can't change the super class!!!");
        }
        CtField[] ctFields = ctClass.getFields();
        CtMethod[] ctMethods = ctClass.getMethods();

        for (ClassMetaModel.DynamicFieldInfo fieldInfo: classMetaModel.getFields()){

            String newFieldName = fieldInfo.getFieldName();
            CtField oldCtField = findCtField(ctFields,newFieldName);

            if (oldCtField == null){
                //add new field
                //addFieldToCls(ctClass,fieldInfo);
                dirty = true;
                break;
            }else {
                //can remove core filed;
                if (newFieldName != RuntimeContextFieldName && newFieldName != RuntimeProxyFieldName) {
                    // if same type with same name do nothing
                    if (!oldCtField.getType().getName().equals(fieldInfo.getFieldType().getName())) {
                        dirty = true;
                        break;
                        //oldCtField.setName(newFieldName + postfix);
                        //addFieldToCls(ctClass,fieldInfo);

                    }
                }
            }
        }

        for (MethodMetaModel methodMetaModel : methodMetaModels){
            String newMethodName = methodMetaModel.getMethodName();
            if (newMethodName.startsWith(SUPERMethodPrefix)){
                continue;
            }

            CtMethod method = findCtMethod(ctMethods,newMethodName);

            if (method == null){
                dirty = true;
                break;
            }


            if (isSameMethodDeclare(method,methodMetaModel)){
                continue;
            }

            //method.setName(newMethodName+postfix);

            //addMethod(ctClass,methodMetaModel);
            dirty = true;
        }

        if (isDebug){
            System.out.println("isDirty:"+dirty);
        }


        if (dirty){
            if (isDebug)
                System.out.println("Rename "+ ctClass.getName() + " to "+ctClass.getName()+postfix);
            ctClass.setName(classMetaModel.getFullName()+postfix);

            //ctClass = pool.get(classMetaModel.getFullName());

            ctClass = newClass(classMetaModel);

            for (MethodMetaModel methodMetaModel:methodMetaModels){
                addMethod(ctClass,methodMetaModel);
            }

            return ctClass;
        }else{
            return null;
        }

        //return  ctClass;
    }

}
