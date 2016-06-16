package cn.net.xyan.blossom.script.test;

import jdk.nashorn.api.scripting.JSObject;
import org.junit.Test;

import javax.script.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import jdk.internal.dynalink.beans.StaticClass;

/**
 * Created by zarra on 16/6/10.
 */
public class NashornTest {
    @Test
    public void doTest() throws IOException, URISyntaxException, ScriptException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        final Compilable compilable = (Compilable) engine;
        final Invocable invocable = (Invocable) engine;

        final String statement = Help.loadScript("f.js");
              //  "function fetch(value, count) { count++ ; return {value: value, count : count} };";
        final CompiledScript compiled = compilable.compile(statement);

        Object result = compiled.eval();

        JSObject jsObject =  (JSObject) invocable.invokeFunction("setter", 10);

        jsObject =  (JSObject) invocable.invokeFunction("getter");

        System.out.println(jsObject);

       // System.out.println("value=" + jsObject.getMember("value"));
       // System.out.println("count=" + jsObject.getMember("count"));



        //System.out.println(result.getClass().getName());
    }
}
