package cn.net.xyan.blossom.storage.test;

import cn.net.xyan.blossom.storage.support.SlashPath;
import org.junit.Test;

/**
 * Created by zarra on 2016/10/19.
 */
public class PathTest {

    @Test
    public void test(){
        SlashPath sp = SlashPath.fromString("/ab/dd/dd/ere/dd");

        int size = sp.getNameCount();

        for (int i=0;i<size;i++){
            SlashPath slashPath = sp.getName(i);
            System.out.println(slashPath.toString());
        }
    }

    @Test
    public void test2(){
        SlashPath sp = SlashPath.fromString("/");

        boolean b = sp.equals(SlashPath.ROOT);

        int size = sp.getNameCount();

        for (int i=0;i<size;i++){
            SlashPath slashPath = sp.getName(i);
            System.out.println(slashPath.toString());
        }
    }
}
