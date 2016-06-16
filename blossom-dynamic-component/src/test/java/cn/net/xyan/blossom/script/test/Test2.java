package cn.net.xyan.blossom.script.test;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by zarra on 16/6/9.
 */
public class Test2 {
    @Test
    public void doTest() throws IOException, URISyntaxException {

        String varToJS = "Hello";

        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        String script = Help.loadScript("f.js");

        Object result = cx.evaluateString(scope, script, null, 1, null);


        Object a = scope.get("a" , scope);

       // if (a instanceof Function){
            Object f = Context.javaToJS(varToJS,scope);
            scope.put("f",scope,f);
            Function functionA = (Function) a;

            Object testArgs[] = {};
            Object r = functionA.call(cx, scope, scope, testArgs);
            System.out.println(Context.toString(r));
       // }

        Assert.assertEquals(r,varToJS);

        Context.exit();

    }
}
