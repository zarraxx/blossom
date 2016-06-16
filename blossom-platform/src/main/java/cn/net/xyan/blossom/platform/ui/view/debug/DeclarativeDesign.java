package cn.net.xyan.blossom.platform.ui.view.debug;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.declarative.ui.*;
import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import cn.net.xyan.blossom.declarative.utils.DynamicMethodProxy;
import cn.net.xyan.blossom.platform.service.UISystemService;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zarra on 16/6/8.
 */
@SpringView(name = "debug.Declarative")
@SideBarItem(sectionId = UISystemService.CatalogDebug, caption = "Declarative", order = 1)
@FontAwesomeIcon(FontAwesome.COGS)
public class DeclarativeDesign extends VerticalLayout implements View,Designer.ComponentPreviewStrategy {

    Logger logger = LoggerFactory.getLogger(DeclarativeDesign.class);

    Designer designer;

    ComponentClassFactory classFactory = new SimpleComponentClassFactoryImpl();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    public DeclarativeDesign() {
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        designer = new Designer();

        //designer.setClassFactory(classFactory);

        designer.setRenderStrategy(this);

        designer.getTextArea().setValue(
                "<vaadin-vertical-layout>\n" +
                "  <vaadin-text-field caption=\"Name\"/>\n" +
                "  <vaadin-text-field caption=\"Street address\"/>\n" +
                "  <vaadin-text-field caption=\"Postal code\"/>\n" +
                "</vaadin-vertical-layout>"
                );

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
