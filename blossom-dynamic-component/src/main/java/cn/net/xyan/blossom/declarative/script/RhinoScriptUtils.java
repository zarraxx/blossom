package cn.net.xyan.blossom.declarative.script;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.declarative.utils.ByteCodeUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zarra on 16/6/10.
 */
public class RhinoScriptUtils {

    static Logger logger = LoggerFactory.getLogger(RhinoScriptUtils.class);

    public static Scriptable readScopeFromScript(InputStream in) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        Context cx = Context.enter();
        Scriptable scope = null;
        try {
            scope = cx.initStandardObjects();
            Object result = cx.evaluateReader(scope, bufferedReader, null, 1, null);

        } finally {
            Context.exit();
        }
        return scope;
    }


    public static Function findFunction(String function, Scriptable scope) {
        Object r = scope.get(function, scope);
        if (r == Scriptable.NOT_FOUND) {
            return null;
        } else {
            if (r instanceof Function) {
                Function f = (Function) r;
                return f;
            } else {
                return null;
            }
        }

    }


    public static Object callRhinoFunction(Function function, Object[] args, Class<?> returnType, Scriptable scope, Context cx) {

        Object javaReturn = null;

        try {
            //Object[] params = new Object[args.length];

//            for (int i = 0; i < params.length; i++) {
//                params[i] = Context.javaToJS(args[0], scope);
//            }
            // Object scriptReturn = function.call(cx, scope, scope, params);
            System.out.println(RhinoScriptUtils.displayScope(scope));
            Object scriptReturn = function.call(cx, scope, scope, args);



            if (scriptReturn == Scriptable.NOT_FOUND) {

            } else {
                if (!ByteCodeUtils.isVoid(returnType) && scriptReturn != null) {

                    System.out.println("return type from script:" + scriptReturn.getClass().getName());

                    if (scriptReturn instanceof Scriptable) {
                        javaReturn = cx.jsToJava(scriptReturn, returnType);
                    } else {
                        javaReturn = scriptReturn;
                    }
                }
            }
        } catch (Throwable e) {
            ExceptionUtils.traceError(e, logger);
            throw e;
        } finally {
            //Context.exit();
        }

        return javaReturn;

    }

    public static void pushContextToScope(RuntimeContext runtimeContext, Scriptable scope) {
        for (String name : runtimeContext.keySet()) {
            if (!RuntimeContext.KEYForSCOPE.equals(name)) {
                Object v = runtimeContext.get(name);
                // Object jsObj = Context.javaToJS(v, scope);
                if (v instanceof RuntimeContext) {
                    continue;//do not put runtime context
                } else {
                    scope.put(name, scope, v);
                }
            }
        }
    }

    public static void popScopeToContext(RuntimeContext runtimeContext, Scriptable scope) {
        for (String name : runtimeContext.keySet()) {
            if (!RuntimeContext.KEYForSCOPE.equals(name)) {
                Class<?> javaType = Object.class;
                Object v = runtimeContext.get(name);
                if (v != null) javaType = v.getClass();

                Object jsObj = scope.get(name, scope);
                Object javaObj = null;
                if (jsObj instanceof Scriptable)
                    javaObj = Context.jsToJava(jsObj, javaType);
                else {
                    if (jsObj != null) {
                        Class<?> jsObjType = jsObj.getClass();
                        if (!javaType.isAssignableFrom(jsObjType)) {
                            Object convertObj = convertObjectTypeFromRhino(javaType, jsObj.getClass(), jsObj);
                            if (convertObj != null)
                                jsObj = convertObj;
                        }
                    }
                    javaObj = jsObj;
                }

                runtimeContext.put(name, javaObj);
            }
        }
    }

    static public <T> T convertObjectTypeFromRhino(Class<T> want, Class<?> jsObjType, Object jsObj) {
        if (Number.class.isAssignableFrom(want) && Number.class.isAssignableFrom(jsObjType)) {

            String jsObjValue = jsObj.toString();
            Double d = new Double(jsObjValue);
            if (Byte.class == want) {
                return (T) (Byte) d.byteValue();
            } else if (Short.class == want) {
                return (T) (Short) d.shortValue();
            } else if (Integer.class == want) {
                return (T) (Integer) d.intValue();
            } else if (Long.class == want) {
                return (T) (Long) d.longValue();
            } else if (Float.class == want) {
                return (T) (Float) d.floatValue();
            } else if (Double.class == want) {
                return (T) d;
            }

        }

        return null;
    }

    static public String displayScope(Scriptable scope) {
        StringBuffer sb = new StringBuffer();
        for (Object id : scope.getIds()) {
            Object jsObj = scope.get(id.toString(), scope);
            sb.append("id:" + id);

            sb.append("\tobj:" + jsObj + "\n");
        }

        return sb.toString();
    }
}
