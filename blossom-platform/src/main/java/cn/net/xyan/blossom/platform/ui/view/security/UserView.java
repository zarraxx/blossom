package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.Sections;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "security.user")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "User", order = 0)
@FontAwesomeIcon(FontAwesome.HOME)
public class UserView  extends VerticalLayout implements View {
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
