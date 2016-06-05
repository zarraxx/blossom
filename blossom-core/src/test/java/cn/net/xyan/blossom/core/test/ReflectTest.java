package cn.net.xyan.blossom.core.test;

import cn.net.xyan.blossom.core.utils.ReflectUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zarra on 16/6/4.
 */
public class ReflectTest {

    static class TestA{
        String a;
        String b;
        String c;
        Integer d;
        public TestA(String a,String b,String c,Integer d){
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public Integer getD() {
            return d;
        }

        public void setD(Integer d) {
            this.d = d;
        }
    }

    static class TestB{
        String b;
        TestA a;

        public TestB(TestA a,String b){
            this.a = a;
            this.b = b;
        }

        public TestA getA() {
            return a;
        }

        public void setA(TestA a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
    @Test
    public void doTest(){
        TestA testA = new TestA("a","b","c",1);

        TestB testB = new TestB(testA,"B");

        String getA = null;

        getA= (String) ReflectUtils.getProperty(testA,"a");

        Assert.assertEquals(getA,"a");

        getA = (String) ReflectUtils.getProperty(testB,"a.a");

        Assert.assertEquals(getA,"a");
    }
}
