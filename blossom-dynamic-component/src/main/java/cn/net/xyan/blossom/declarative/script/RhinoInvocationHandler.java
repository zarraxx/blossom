package cn.net.xyan.blossom.declarative.script;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zarra on 16/6/10.
 */
public class RhinoInvocationHandler implements InvocationHandler {

    RuntimeContext runtimeContext;


    public RhinoInvocationHandler(RuntimeContext context){
        this.runtimeContext = context;
    }

    public RhinoInvocationHandler(){

    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (runtimeContext == null) throw new NullPointerException("runtimeContext must not null");

        Scriptable scope = (Scriptable) runtimeContext.get(RuntimeContext.KEYForSCOPE);

        if (scope == null) throw new NullPointerException("scope must not null");

        String methodName = method.getName();

        Function function = RhinoScriptUtils.findFunction(methodName,scope);

        if (function == null) throw new NullPointerException("function must not null");

        Object returnValue = null;
        Context cx = Context.enter();

        try{

            //push java variable to rhino
            RhinoScriptUtils.pushContextToScope(runtimeContext,scope);

            RuntimeContext writeableRuntimeContext = (RuntimeContext) runtimeContext.get(RuntimeContext.KEYForWriteable);
            if (writeableRuntimeContext!=null){
                RhinoScriptUtils.pushContextToScope(writeableRuntimeContext,scope);
            }


            returnValue = RhinoScriptUtils.callRhinoFunction(function,args,Object.class,scope,cx);

            //pop rhino obj to java
            if (writeableRuntimeContext!=null){
                RhinoScriptUtils.popScopeToContext(writeableRuntimeContext,scope);
            }

        }catch (Throwable e){
           // String msg = e.getMessage();
            System.err.println(ExceptionUtils.errorString(e));
            //Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }
        finally {
            Context.exit();
        }

        return returnValue;
    }
}
