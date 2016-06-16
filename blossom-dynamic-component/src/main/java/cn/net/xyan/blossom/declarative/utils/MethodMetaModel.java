package cn.net.xyan.blossom.declarative.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/9.
 */
public class MethodMetaModel {
    String methodName;
    Class<?> returnType;
    List<Class<?>> paramClass = new LinkedList<>();

    List<String>   paramNames = new LinkedList<>();

    List<Class<? extends Throwable>> throwableCls = new LinkedList<>();


    public MethodMetaModel(){

    }

    public MethodMetaModel(String name, Class<?> returnType){
        this.methodName = name;
        this.returnType = returnType;
    }

    public void addParam(String name,Class<?> type){
        getParamClass().add(type);
        getParamNames().add(name);
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Class<?>> getParamClass() {
        return paramClass;
    }

    public void setParamClass(List<Class<?>> paramClass) {
        this.paramClass = paramClass;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    public  List<Class<? extends Throwable>> getThrowableCls() {
        return throwableCls;
    }

    public void setThrowableCls( List<Class<? extends Throwable>> throwableCls) {
        this.throwableCls = throwableCls;
    }

    static String join(List<String> stringList, String sep){
        if (stringList.size()>0){

            if (stringList.size() == 1){
                return stringList.get(0);
            }else {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i=0;i<stringList.size();i++){
                    if (i >0){
                        stringBuffer.append(sep);
                    }
                    stringBuffer.append(stringList.get(i));
                }
                return stringBuffer.toString();
            }

        }else{
            return "";
        }
    }

    public String generateMethodDeclare(){
        String returnTypeName = "void";
        if (!getReturnType().equals(Void.TYPE) && Void.class!=getReturnType())
            returnTypeName = getReturnType().getName();

        String throwString = "";

        if (getThrowableCls().size()>0) {
            List<String> throwableDeclares = new LinkedList<>();
            for (Class<?extends Throwable> tc : getThrowableCls()){
                throwableDeclares.add(tc.getName());
            }
            throwString = join(throwableDeclares,",");

            throwString = "throws "+ throwString;
        }

        String format = "public %s %s ( %s ) %s\n";
        List<String> paramDeclares = new LinkedList<>();

        for (int i=0;i<getParamNames().size();i++){

            Class<?> paramType = getParamClass().get(i);
            String paramTypeString = paramType.getName();

            if (paramType.isArray()){
                Class<?> cClass = paramType.getComponentType();
                paramTypeString = cClass.getName()+"[]";
            }

            paramDeclares.add(
                    String.format("%s %s",paramTypeString,getParamNames().get(i) )
            );
        }
        String list = join(paramDeclares,",");
        return String.format(format,returnTypeName,getMethodName(),list,throwString);
    }

    public String generateMethodBody(){
        int size = getParamNames().size()+1;
        String format = "{\n\tObject[][] p =  new Object[%d][2]; \n%s \n\t;\n\t%s%s.invoke(this,\"%s\",p);\n}";

        String returnString = "";

        if (!getReturnType().equals(Void.TYPE) && Void.class!=getReturnType()) {

            returnString = String.format("return (%s)", getReturnType().getName());
        }


        List<String> list = new LinkedList<>();

        list.add(String.format("\tp[0]=new Object[]{\"%s\",\"%s\"}",ByteCodeUtils.RETURNTypeKey,getReturnType().getName()));
        int index = 1;
        for (String name : getParamNames()){
            list.add( String.format( "\tp[%d]=new Object[]{\"%s\",%s}",index,name,name));
            index++;
        }






        String setP = join(list,";\n");

        return String.format(format,size,setP,returnString,ByteCodeUtils.RuntimeProxyFieldName,getMethodName());
    }
}
