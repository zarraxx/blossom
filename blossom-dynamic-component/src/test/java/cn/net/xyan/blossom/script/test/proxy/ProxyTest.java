package cn.net.xyan.blossom.script.test.proxy;

import cn.net.xyan.blossom.declarative.script.RhinoDynamicMethodProxy;
import cn.net.xyan.blossom.declarative.script.RhinoScriptUtils;
import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.script.test.Help;
import org.junit.Test;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/10.
 */
public class ProxyTest {

    public static class SimpleBean{

        public SimpleBean(){

        }

        public SimpleBean(int i,String s){
            this.i = i;

            this.s = s;
        }

        int i;
        String s;

        List<String> strings = new LinkedList<>();

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }

        public void addString(String s){
            getStrings().add(s);
        }

        public void print(Object o){
            System.out.println(o.toString());
        }
    }

    @Test
    public void doTest() throws IOException {

        SimpleBean bean = new SimpleBean(100,"9999");

        InputStream inputStream = Help.loadInputStream("proxy.js");

        RhinoDynamicMethodProxy proxy = new RhinoDynamicMethodProxy();

        RuntimeContext runtimeContext = new RuntimeContext();

        Scriptable scope = RhinoScriptUtils.readScopeFromScript(inputStream,runtimeContext);

        SampleClass s = new SampleClass();

        proxy.bindToObject(s,scope);


        s.DynamicMethodAvailableGetRuntimeContext().put("bean",bean);

        //scope.put("bean",scope,bean);



        System.out.println(RhinoScriptUtils.displayScope(scope));


        s.df1(123);

        String v = s.df2("ttkjk");


        System.out.println(v);

        System.out.println(bean.getI());

        System.out.println(bean.getS());



    }
}
