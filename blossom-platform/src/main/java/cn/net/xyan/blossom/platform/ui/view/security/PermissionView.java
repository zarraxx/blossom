package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/2.
 */
@SpringView(name = "security.permission")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "Permission", order = 1)
@FontAwesomeIcon(FontAwesome.CERTIFICATE)
public class PermissionView extends EntityView<Permission>{

    @Autowired
    I18NService i18NService;

    public PermissionView(){
       super(TR.m("ui.view.security.permission.caption","Permission"));
    }



    @Override
    public void saveEntity(EntityItem<Permission> bi) {


        super.saveEntity(bi);

        Permission permission = bi.getEntity();

        String titleKey = Permission.i18nTitleKey(permission);

        String describeKey = permission.i18nDescribeKey(permission);

        I18NString title = i18NService.setupMessage(titleKey,permission.getCode());

        I18NString describe = i18NService.setupMessage(describeKey,"");

        permission.setTitle(title);

        permission.setDescribe(describe);

        getContainer().addEntity(permission);


    }
}
