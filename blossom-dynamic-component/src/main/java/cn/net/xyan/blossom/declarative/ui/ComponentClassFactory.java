package cn.net.xyan.blossom.declarative.ui;


import com.vaadin.ui.Component;

import java.io.InputStream;


/**
 * Created by zarra on 16/6/9.
 */
public interface ComponentClassFactory {

    ComponentClassMetaModel parse(String className,InputStream inputStream) throws ClassNotFoundException;
    Class<? extends Component> compileToJavaClass(ComponentClassMetaModel componentClassMetaModel) throws Exception;

}
