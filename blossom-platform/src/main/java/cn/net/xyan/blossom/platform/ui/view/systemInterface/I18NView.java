package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.platform.dao.I18NStringDao;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/2.
 */
@SpringView(name = "interface.i18n")
@SideBarItem(sectionId = UISystemService.CatalogI18n, caption = "I18NString", order = 0)
@FontAwesomeIcon(FontAwesome.LANGUAGE)
public class I18NView extends VerticalLayout implements View {

    JPAContainer<I18NString> container;

    Table table;

    @Autowired
    I18NStringDao stringDao;

    Logger logger = LoggerFactory.getLogger(I18NView.class);

    public I18NView(){

        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.i18n.string.caption","Manager I18NString!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        table = new Table();

        container = EntityContainerFactory.jpaContainer(I18NString.class);




        addComponent(table);

        setExpandRatio(table,1);



    }


    @Override
    public void attach() {

        super.attach();
        table.setContainerDataSource(container);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
