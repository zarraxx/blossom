package cn.net.xyan.blossom.declarative.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by zarra on 16/6/9.
 */
public  class ClassMetaModel {


    public static Set<ConstructorParams> parseClsConstructor(Class<?> cls){

        Set<ConstructorParams> result = new HashSet<>();

        for (Constructor constructor:cls.getConstructors()){
            ConstructorParams constructorParams = new ConstructorParams();
            for(Parameter p : constructor.getParameters()){
                constructorParams.add((Class<Object>) p.getType());
            }
        }

        return  result;
    }

    public static class ConstructorParams extends LinkedList<Class<Object>>{

    }

    public static class DynamicFieldInfo{
        public DynamicFieldInfo(){

        }

        public DynamicFieldInfo(String fieldName,Class<?> fieldType){
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }
        Class<?> fieldType;
        String fieldName;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public void setFieldType(Class<?> fieldType) {
            this.fieldType = fieldType;
        }
    }

    public ClassMetaModel(String fullName){
        this(fullName,Object.class);
    }

    public ClassMetaModel(String fullName, Class<?> superClass){
        this.fullName = fullName;
        this.superClass = superClass;
        this.constructors = parseClsConstructor(superClass);
    }

    String fullName;
    Class<?> superClass;
    Set<ConstructorParams> constructors = new HashSet<>();
    List<DynamicFieldInfo> fields = new LinkedList<>();

    List<Class<? extends Annotation>> annotations = new LinkedList<>();

    public Set<ConstructorParams> getConstructorParams() {
        return constructors;
    }

    public void setConstructorParams(Set<ConstructorParams> constructorParams) {
        this.constructors = constructorParams;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<DynamicFieldInfo> getFields() {
        return fields;
    }

    public void setFields(List<DynamicFieldInfo> fields) {
        this.fields = fields;
    }

    public Class<?> getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Class<? extends Annotation>> annotations) {
        this.annotations = annotations;
    }
}
