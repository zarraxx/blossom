package cn.net.xyan.blossom.platform;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zarra on 16/6/5.
 */
public class LoadTest {

    @Test
    public void doLoadTest(){
        ClassLoader classLoader = getClass().getClassLoader();


        //java.net.URL url = getClass().getResource("/VAADIN/widgetsets/cn.net.xyan.blossom.platform.BlossomUI/cn.net.xyan.blossom.platform.BlosssomUI.nocache.js");
        //java.net.URL url = getClass().getResource("/VAADIN/widgetsets/cn.net.xyan.blossom.platform.BlossomUI/aosp.manifest");
//        try {
//            //InputStream in = url.openStream();
////            if (in == null){
////
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
