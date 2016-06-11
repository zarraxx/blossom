package cn.net.xyan.blossom.script.test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by zarra on 16/6/9.
 */
public class RhinoTest {




    public static class J{
        static Integer add(Integer a,Integer b){
            return a+b;
        }
    }

    @Test
    public void doTest() throws IOException, URISyntaxException, NoSuchMethodException {

        String className = getClass().getName();

        className.replace(".","/");
        String script = Help.loadScript("test.js");

        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        Method method = J.class.getDeclaredMethod("add",Integer.class,Integer.class);
        Method println = PrintStream.class.getDeclaredMethod("println",String.class);

        Object systemOut = Context.javaToJS(System.out,scope);

        scope.put("out",scope,systemOut);

        FunctionObject addFunction = new FunctionObject(method.getName(), method, scope);
        FunctionObject printFunction = new FunctionObject(method.getName(), method,scope);
        FunctionObject.putProperty(scope, method.getName(), addFunction);

        Scriptable child = cx.newObject(scope);

        scope.put("F",scope,child);

        child.put("add",child,addFunction);

        Object result = cx.evaluateString(scope, script, null, 1, null);

        Object jsObject = scope.get("context" , scope);
        Object attach = null;
        if (jsObject instanceof Scriptable){
            Scriptable cc = (Scriptable) jsObject;
             attach  = cc.get("onAttach" , cc);
        }
        Object r = scope.get("r",scope);
        System.out.println("jsobject is a class of:"+jsObject.getClass().getName());
        if (r == Scriptable.NOT_FOUND) {
            System.out.println("test is not defined.");
        } else {
            System.out.println("test is " + Context.toString(r));
        }


        Context.exit();


    }
}
