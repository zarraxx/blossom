package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
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
public class PermissionView extends EntityView<Permission>{
    public PermissionView(){
       super(TR.m("ui.view.security.permission.caption","Permission"));
    }

}
