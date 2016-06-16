package cn.net.xyan.blossom.declarative.ui;

import cn.net.xyan.blossom.declarative.script.RuntimeContext;
import cn.net.xyan.blossom.declarative.script.UIContext;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by zarra on 16/6/10.
 */
public class ComponentFactory {



    public static Component makeFromHtml(String xml ,RuntimeContext runtimeContext){
        byte[] buffer = xml.getBytes();

        InputStream inputStream  = new ByteArrayInputStream(buffer);


        DesignContext dc =  Design.read(inputStream,null);

        Component c = dc.getRootComponent();

        UIContext scriptContext = new UIContext();

        scriptContext.setRuntimeContext(runtimeContext);

        scriptContext.setDc(dc);

        scriptContext.setRoot(c);

        scriptContext.load(xml);

        return c;
    }
}
