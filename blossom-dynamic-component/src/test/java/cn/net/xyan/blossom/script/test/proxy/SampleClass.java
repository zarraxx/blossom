package cn.net.xyan.blossom.script.test.proxy;

import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodAvailable;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy;

/**
 * Created by zarra on 16/6/10.
 */
public class SampleClass implements DynamicMethodAvailable{

    RuntimeContext runtimeContext;

    DynamicMethodProxy proxy;


    @Override
    public void DynamicMethodAvailableSetProxy(DynamicMethodProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public DynamicMethodProxy DynamicMethodAvailableGetProxy() {
        return this.proxy;
    }

    @Override
    public void DynamicMethodAvailableSetRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public RuntimeContext DynamicMethodAvailableGetRuntimeContext() {
        return this.runtimeContext;
    }

    public Object callProxy(String methodName,Class<?> returnType,Object ... params){
        Object[][] p = new Object[params.length+1][2];
        p[0] = new Object[]{"__return__type__",returnType.getName()};
        for (int i=0; i< params.length;i++){

            p[i+1] = new Object[]{new Integer(i),params[i]};
        }

        return proxy.invoke(this,methodName,p);

    }

    public String nf1(){
        return "nf1";
    }

    public String nf2(String a){
        return a;
    }

    public void df1(Integer i){
        callProxy("df1",Void.class,i);
    }

    public String df2(String a){
        Object v =  callProxy("df2",String.class,a);

        return (String) v;
    }
}
