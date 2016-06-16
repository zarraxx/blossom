package cn.net.xyan.blossom.declarative.ui;

import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodAvailable;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.themes.ValoTheme;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zarra on 16/6/9.
 */
public class Designer extends VerticalLayout {

    Logger logger = LoggerFactory.getLogger(Designer.class);

    TextArea textArea;

    Button bPreview;

    ComponentPreviewStrategy renderStrategy;

    ComponentFactory componentFactory ;


    public interface ComponentPreviewStrategy {
        void render(Component c);

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

        componentFactory = new ComponentFactory();

        bPreview.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (getRenderStrategy() != null ) {
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
            Component c = componentFactory.makeFromHtml(xml,null);

            renderStrategy.render(c);

//            Button button = new Button();
//            button.addClickListener(new Button.ClickListener() {
//                @Override
//                public void buttonClick(Button.ClickEvent event) {
//
//                }
//            });
            //button.addClickListener();

        } catch (Throwable e) {
            String msg = e.getMessage();

            Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }
    }




}
