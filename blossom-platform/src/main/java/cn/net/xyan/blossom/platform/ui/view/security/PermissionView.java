package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/2.
 */
@SpringView(name = "security.permission")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "Permission", order = 1)
@FontAwesomeIcon(FontAwesome.CERTIFICATE)
public class PermissionView extends VerticalLayout implements View {
    public PermissionView(){
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.permission.caption","Manager Permission!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);
    }
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
