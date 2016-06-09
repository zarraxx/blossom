package cn.net.xyan.blossom.declarative.ui;

import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodAvailable;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.themes.ValoTheme;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by zarra on 16/6/9.
 */
public class Designer extends VerticalLayout {

    TextArea textArea;

    Button bPreview;

    ComponentPreviewStrategy renderStrategy;

    ComponentClassFactory classFactory;

    ComponentClassMetaModel componentClassMetaModel;

    public interface ComponentPreviewStrategy {
        void render(Component c);

        String createClassName();

        DynamicMethodProxy methodProxy(ComponentClassMetaModel classMetaModel);

        RuntimeContext createRuntimContext(ComponentClassMetaModel classMetaModel);
    }

    public Button getbPreview() {
        return bPreview;
    }

    public void setbPreview(Button bPreview) {
        this.bPreview = bPreview;
    }

    public ComponentPreviewStrategy getRenderStrategy() {
        return renderStrategy;
    }

    public void setRenderStrategy(ComponentPreviewStrategy renderStrategy) {
        this.renderStrategy = renderStrategy;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public ComponentClassFactory getClassFactory() {
        return classFactory;
    }

    public void setClassFactory(ComponentClassFactory classFactory) {
        this.classFactory = classFactory;
    }

    public Designer() {
        setSizeFull();
        setSpacing(true);
        //setMargin(true);

        Label header = new Label("Declarative UI Designer");
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        Label hr = new Label("<hr/>", ContentMode.HTML);

        addComponent(hr);

        bPreview = new Button("Preview");

        addComponent(bPreview);

        textArea = new TextArea("XML");

        textArea.setSizeFull();

        addComponent(textArea);

        setExpandRatio(textArea, 1);

        bPreview.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (getRenderStrategy() != null || getClassFactory() == null) {
                    String xml = textArea.getValue();
                    doPreview(xml);
                } else {
                    Notification.show("No renderStrategy or classFactory set.!", Notification.Type.ERROR_MESSAGE);
                }
            }
        });


    }

    protected void doPreview(String xml) {
        try {
            Class<? extends Component> componentClass = complieXMLToJavaType(xml);
            Component component = componentClass.newInstance();
            component = bindComponent(component,xml);

            renderStrategy.render(component);
        } catch (Throwable e) {
            String msg = e.getMessage();

            Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }
    }

    protected Component bindComponent(Component c, String xml) {
        try {
            byte[] bytes = xml.getBytes();
            InputStream in = new ByteArrayInputStream(bytes);
            Design.read(in, c);



            if (c instanceof DynamicMethodAvailable) {
                DynamicMethodAvailable dynamicComp = (DynamicMethodAvailable) c;
                RuntimeContext runtimeContext = renderStrategy.createRuntimContext(componentClassMetaModel);
                DynamicMethodProxy proxy = renderStrategy.methodProxy(componentClassMetaModel);
                if (runtimeContext != null) {
                    runtimeContext.put("meta",componentClassMetaModel);
                    dynamicComp.DynamicMethodAvailableSetRuntimeContext(runtimeContext);
                    if (proxy!=null){
                        dynamicComp.DynamicMethodAvailableSetProxy(proxy);
                    }
                }

            }

            return c;
        } catch (Throwable e) {
            String msg = e.getMessage();

            Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }
        return null;
    }

    protected Class complieXMLToJavaType(String xml) {
        try {
            byte[] bytes = xml.getBytes();
            InputStream in = new ByteArrayInputStream(bytes);
            String classname = renderStrategy.createClassName();
            ComponentClassMetaModel componentClassMetaModel = classFactory.parse(classname, in);
            this.componentClassMetaModel = componentClassMetaModel;
            return classFactory.compileToJavaClass(componentClassMetaModel);

        } catch (Throwable e) {
            String msg = e.getMessage();

            Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }
        return null;
    }
}
