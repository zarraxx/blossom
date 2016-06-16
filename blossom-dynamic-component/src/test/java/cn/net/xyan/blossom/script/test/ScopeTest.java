package cn.net.xyan.blossom.script.test;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import org.mozilla.javascript.NativeObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by zarra on 16/6/10.
 */
public class ScopeTest {

    @Test
    public void doTest() throws IOException, URISyntaxException {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        String script = Help.loadScript("scope.js");

        Object result = cx.evaluateString(scope, script, null, 1, null);


        Object s1 = scope.get("s1" , scope);

        Object s2 = scope.get("s2" , scope);


        if (s1 instanceof Scriptable){
            Scriptable scopeS1 = (Scriptable) s1;

            for(Object id : scopeS1.getIds()){
                System.out.println("id:"+id.toString());
            }

            Object f1 = scopeS1.get("f1",scopeS1);
        }





        Context.exit();
    }
}
