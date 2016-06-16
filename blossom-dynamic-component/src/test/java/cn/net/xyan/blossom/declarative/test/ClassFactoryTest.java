package cn.net.xyan.blossom.declarative.test;

import cn.net.xyan.blossom.declarative.ui.ComponentClassFactory;
import cn.net.xyan.blossom.declarative.ui.ComponentClassMetaModel;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by zarra on 16/6/9.
 */
public class ClassFactoryTest {

    static final public String xml = "<vaadin-vertical-layout size-full>\n" +
            "  <!-- Align right in the containing layout -->\n" +
            "  <vaadin-label width-auto :right>Hello!</vaadin-label>\n" +
            "\n" +
            "  <!-- Expands to take up all remaining vertical space -->\n" +
            "  <vaadin-horizontal-layout size-full :expand>\n" +
            "    <!-- Automatic width - shrinks horizontally -->\n" +
            "    \n" +
            "\n" +
            "<vaadin-tree _id=\"mytree\" caption=\"My Tree\"/>\n" +
            "\n\n" +
            "\n" +
            "    <!-- Expands horizontally to take remaining space -->\n" +
            "    <vaadin-table _id=\"mytable\" size-full :expand/>\n" +
            "  </vaadin-horizontal-layout>\n" +
            "</vaadin-vertical-layout>";


    static final public String xml2 = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta name=\"package-mapping\"\n" +
            "          content=\"my:com.example.myapp\" />\n" +
            "  </head>\n" +
            "\n" +
            "  <body>\n" +
            "    <vaadin-vertical-layout>\n" +
            "      <vaadin-label><b>Hello!</b> -\n" +
            "                How are you?</vaadin-label>\n" +
            "\n" +
            "      <!-- Use it here -->\n" +
            "      <my-example-component _id=\"mycomp\"/>\n" +
            "    </vaadin-vertical-layout>\n" +
            "  </body>\n" +
            "</html>";


   // @Test
//    public void doTest() throws Exception {
//
//        ComponentClassFactory componentClassFactory = new SimpleComponentClassFactoryImpl();
//
//        ComponentClassMetaModel metaModel = new ComponentClassMetaModel("cc.c.c.c.dfsdaf.e123", VerticalLayout.class);
//
//        metaModel.addField("table", Table.class);
//
//        metaModel.addField("cb", ComboBox.class);
//
//        Class createClass = componentClassFactory.compileToJavaClass(metaModel);
//
//    }
//
//    @Test
//    public void parseXMLTest() throws Exception {
//        ComponentClassFactory componentClassFactory = new SimpleComponentClassFactoryImpl();
//        byte[] bytes = xml2.getBytes();
//        InputStream in = new ByteArrayInputStream(bytes);
//        ComponentClassMetaModel model = componentClassFactory.parse("test.a.b.c.d.e.f.g",in);
//
//        Class createClass = componentClassFactory.compileToJavaClass(model);
//
//        Component c = (Component) createClass.newInstance();
//
//        in = new ByteArrayInputStream(bytes);
//
//        //c = new CustomComponent();
//
//        DesignRoot designRoot = (DesignRoot) createClass.getAnnotation(DesignRoot.class);
//
//        Assert.assertNotNull(designRoot);
//
//        Design.read(in,c);
//
//    }
}
