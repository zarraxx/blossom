package cn.net.xyan.blossom.platform.ui.view.security;


import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Group;

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
 * Created by zarra on 16/6/1.
 */
@SpringView(name = "security.group")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "Group", order = 2)
@FontAwesomeIcon(FontAwesome.GROUP)
public class GroupView extends EntityView<Group> {

    @Autowired
    I18NService i18NService;

    public GroupView(){
        super("Group");
    }

    @Override
    public void saveEntity(EntityItem<Group> bi) {


        super.saveEntity(bi);

        Group group = bi.getEntity();

        String titleKey = Group.i18nTitleKey(group);

        String describeKey = Group.i18nDescribeKey(group);

        I18NString title = i18NService.setupMessage(titleKey,group.getCode());

        I18NString describe = i18NService.setupMessage(describeKey,"");

        group.setTitle(title);

        group.setDescribe(describe);

        getContainer().addEntity(group);


    }

}
