package cn.net.xyan.blossom.script.test.proxy;

import cn.net.xyan.blossom.declarative.script.RhinoInvocationHandler;
import cn.net.xyan.blossom.declarative.script.RhinoScriptUtils;
import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.declarative.script.ScriptBeanFactory;
import cn.net.xyan.blossom.script.test.Help;
import org.junit.Test;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;

/**
 * Created by zarra on 16/6/10.
 */
public class DynamicInterfaceTest {

    static public class MyScriptable extends ScriptableObject{

        @Override
        public String getClassName() {
            return null;
        }
    }

    @Test
    public void doTest() throws Exception {
        InputStream inputStream = Help.loadInputStream("interface.js");

        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.put("out",System.out);


        ProxyTest.SimpleBean bean = new ProxyTest.SimpleBean(1,"a");

        runtimeContext.putVariable("i",new Integer(1));
        runtimeContext.putVariable("bean",bean);

       // Scriptable scope = RhinoScriptUtils.readScopeFromScript(inputStream,runtimeContext);

       // runtimeContext.setScope(scope);



      //  RhinoInvocationHandler invocationHandler = new RhinoInvocationHandler(runtimeContext);


        Runnable runnable = ScriptBeanFactory.createBean(Runnable.class,inputStream,runtimeContext);

        runnable.run();

        Scriptable scope = runtimeContext.getScope();

        Integer i = (Integer) runtimeContext.getVariable("i");

         bean = (ProxyTest.SimpleBean) runtimeContext.getVariable("bean");

        System.out.println(RhinoScriptUtils.displayScope(scope));


        System.out.println("bean.i = "+ bean.getI());

        System.out.println("bean.s = "+ bean.getS());

        System.out.println(i);

    }
}
