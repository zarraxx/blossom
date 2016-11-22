package cn.net.xyan.blossom.storage.ui.view;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.service.StorageService;
import cn.net.xyan.blossom.storage.ui.component.Directory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.util.List;

/**
 * Created by zarra on 2016/10/19.
 */
@SpringView(name = "debug.fileSystem")
@SideBarItem(sectionId = UISystemService.CatalogDebug, caption = "FileSystem", order = 9)
@FontAwesomeIcon(FontAwesome.FILE)
public class FileSystemView extends VerticalLayout implements View,InitializingBean {

    @Autowired
    StorageService storageService;

    HorizontalLayout buttonLayout;

    Directory directory;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public FileSystemView(){

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.debug.fileSystem.caption", "File System"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        Label hr = new Label("<hr/>", ContentMode.HTML);

        buttonLayout = new HorizontalLayout();

        Button newFile = new Button("New");
        Button listFile = new Button("List");

        newFile.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                storageService.mkdir(null,"home");
            }
        });

        listFile.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<Node>  children = storageService.findAll(null);
                for (Node node : children) {
                    System.out.println(node.getTitle());
                }
            }
        });

        buttonLayout.addComponent(newFile);
        buttonLayout.addComponent(listFile);

        addComponent(buttonLayout);

        directory = new Directory(storageService);

        addComponent(directory);

    }
}
