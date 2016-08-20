package cn.net.xyan.blossom.declarative.script;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by zarra on 16/6/10.
 */
public class ScriptFactoryBean<IT> implements FactoryBean<IT> {


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

    Class<IT> interfaceCls;
    InputStream in;
    RuntimeContext runtimeContext;

    boolean singleton = true;

    Logger logger = LoggerFactory.getLogger(ScriptFactoryBean.class);

    public ScriptFactoryBean(){
       this(null,null);
    }

    public ScriptFactoryBean(Class<IT> interfaceCls, InputStream in) {
        this.interfaceCls = interfaceCls;
        this.in = in;
        runtimeContext = new RuntimeContext();
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public Class<IT> getInterfaceCls() {
        return interfaceCls;
    }

    public void setInterfaceCls(Class<IT> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public IT getObject() throws Exception {
        if (getInterfaceCls() == null)
            throw new NullPointerException();
        if (getIn() == null)
            throw new NullPointerException();
        IT result;
        try {
             result = createBean(getInterfaceCls(), getIn(), getRuntimeContext());
        }catch (Throwable e){
            logger.error("create Bean Error.Bean Type"+ getInterfaceCls().getName());
            logger.error(ExceptionUtils.errorString(e));
            throw  e;
        }

        return result;
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceCls();
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
