package cn.net.xyan.blossom.sample.view;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.declarative.ui.*;
import cn.net.xyan.blossom.sample.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zarra on 16/6/10.
 */
@SpringView(name = "designer")
@SideBarItem(sectionId = Sections.VIEWS, caption = "Designer", order = 1)
@FontAwesomeIcon(FontAwesome.CODE)
public class DesignerView extends VerticalLayout implements View,Designer.ComponentPreviewStrategy {

    Logger logger = LoggerFactory.getLogger(DesignerView.class);

    Designer designer;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    public DesignerView() {
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        designer = new Designer();

        designer.setRenderStrategy(this);

        designer.getTextArea().setValue("<!DOCTYPE html>\n" +
                "<html>\n" +
                " <head>\n" +
                "<script>\n" +
                "var init = function(){\n" +
                "}\n" +
                "__root__.caption=\"123\";\n" +
                "\n" +
                "label.value = \"Welcome!\";\n" +
                "var onClick = { buttonClick: function ( e ) {button.caption = \"welcome\" } }\n" +
                "var listener = new com.vaadin.ui.Button.ClickListener(onClick)\n" +
                "\n" +
                "button.addClickListener(listener);\n" +
                "\n" +
                "</script>  \n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "  <body>\n" +
                "    <vaadin-vertical-layout>\n" +
                "      <vaadin-label id=\"label\"><b>Hello!</b> How are you?</vaadin-label>\n" +
                "      <v-button id=\"button\">OK</v-button>\n" +
                "    </vaadin-vertical-layout>\n" +
                "  </body>\n" +
                "</html>");

        addComponent(designer);

        setExpandRatio(designer,1);

    }

    public void loadDesign(){
        try {

        } catch (Throwable e) {
            ExceptionUtils.traceError(e,logger);
        }
    }

    @Override
    public void render(Component c) {

        VerticalLayout container = new VerticalLayout();

        container.addComponent(c);
        c.setSizeFull();

        container.setSizeFull();

        container.setMargin(true);

        Window window = new Window();
        window.setContent(container);

        window.setCaption("Preview:");
        window.setWidth("400px");
        window.setHeight("600px");

        window.setVisible(true);
        UI.getCurrent().addWindow(window);
    }


}
