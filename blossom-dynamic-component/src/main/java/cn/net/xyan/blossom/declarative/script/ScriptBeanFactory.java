package cn.net.xyan.blossom.declarative.script;

import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by zarra on 16/6/10.
 */
public class ScriptBeanFactory {


    public static <I> I createBean(Class<I> interfaceCls, InputStream in) throws Exception {
        RuntimeContext runtimeContext = new RuntimeContext();
        return createBean(interfaceCls,RhinoInvocationHandler.class,in,runtimeContext);
    }

    public static <I> I createBean(Class<I> interfaceCls, InputStream in,RuntimeContext runtimeContext) throws Exception {
        return createBean(interfaceCls,RhinoInvocationHandler.class,in,runtimeContext);
    }

    public static <I,R extends InvocationHandler> I createBean(Class<I> interfaceCls, Class<R> runtimeCls, InputStream in) throws Exception {
        RuntimeContext runtimeContext = new RuntimeContext();
        return createBean(interfaceCls,runtimeCls,in,runtimeContext);
    }

    public static <I,R extends InvocationHandler> I createBean(Class<I> interfaceCls, Class<R> runtimeCls,
                                                               InputStream in, RuntimeContext runtimeContext) throws Exception {
        Scriptable scope = RhinoScriptUtils.readScopeFromScript(in,runtimeContext);
        runtimeContext.setScope(scope);
        InvocationHandler invocationHandler = null;
        try{
            Constructor<R> constructor = runtimeCls.getConstructor(RuntimeContext.class);
            invocationHandler = constructor.newInstance(runtimeContext);
        } catch (NoSuchMethodException e) {
            throw  e;
        }


        I  interfaceObj = (I) Proxy.newProxyInstance(interfaceCls.getClassLoader(),
                new Class[] { interfaceCls },
                invocationHandler);

        return interfaceObj;
    }
}
