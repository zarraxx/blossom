package cn.net.xyan.blossom.declarative.script;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodAvailable;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by zarra on 16/6/10.
 */
public class RhinoDynamicMethodProxy implements DynamicMethodProxy {


    Logger logger = LoggerFactory.getLogger(RhinoDynamicMethodProxy.class);

    public RhinoDynamicMethodProxy() {
    }

    public void bindToObject(DynamicMethodAvailable obj, Scriptable scope) {
        if (scope == null) throw new NullPointerException("the scope is null");
        RuntimeContext runtimeContext = obj.DynamicMethodAvailableGetRuntimeContext();
        if (runtimeContext == null) {
            runtimeContext = new RuntimeContext();
            obj.DynamicMethodAvailableSetRuntimeContext(runtimeContext);
        }
        runtimeContext.put(RuntimeContext.KEYForSCOPE, scope);
        obj.DynamicMethodAvailableSetProxy(this);
    }

    public void bindToObject(DynamicMethodAvailable obj, InputStream in) throws IOException {
        Scriptable scope = RhinoScriptUtils.readScopeFromScript(in);
        bindToObject(obj, scope);
    }

    @Override
    public Object invoke(DynamicMethodAvailable from, String methodName, Object[][] params) {
        RuntimeContext runtimeContext = from.DynamicMethodAvailableGetRuntimeContext();
        Object returnValue = null;
        try {
            Context cx = Context.enter();
            Scriptable scope = (Scriptable) runtimeContext.get(RuntimeContext.KEYForSCOPE);

            Function function = RhinoScriptUtils.findFunction(methodName, scope);

            if (function != null) {

                RhinoScriptUtils.pushContextToScope(runtimeContext, scope);

                int paramLength = params.length;

                String returnTypeClassName = (String) params[0][1];
                Class<?> returnType = Void.class;

                if (!returnTypeClassName.equals("void") && !Void.class.getName().equals(returnTypeClassName)) {
                    returnType = Class.forName(returnTypeClassName);
                }


                Object[] javaParams = new Object[paramLength];

                javaParams[0] = from;

                for (int i = 1; i < paramLength; i++) {
                    javaParams[i] = params[i][1];
                }

                returnValue = RhinoScriptUtils.callRhinoFunction(function, javaParams, returnType, scope, cx);

                RhinoScriptUtils.popScopeToContext(runtimeContext, scope);

            } else {
                throw new UnsupportedOperationException(methodName);
            }
        } catch (Throwable e) {
            ExceptionUtils.traceError(e, logger);
        } finally {
            Context.exit();
        }

        return returnValue;

    }


}
