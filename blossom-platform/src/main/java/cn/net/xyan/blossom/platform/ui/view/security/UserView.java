package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.Sections;
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
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "security.user")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "User", order = 0)
@FontAwesomeIcon(FontAwesome.USER)
public class UserView  extends VerticalLayout implements View {

    public UserView (){
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.user.caption","Manager user!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
