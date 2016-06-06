package cn.net.xyan.blossom.platform.ui.view.security;


import cn.net.xyan.blossom.platform.dao.UserDao;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.util.Date;

/**
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "security.user")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "User", order = 0)
@FontAwesomeIcon(FontAwesome.USER)
public class UserView  extends EntityView<User> {

    @Autowired
    UserDao userDao;

    public UserView (){
        super("User");
    }

    @Override
    public void saveEntity(EntityItem<User> bi) {
        User user = bi.getEntity();
        if (user.getLoginName()!= null) {
            User databaseUser = userDao.findOne(user.getLoginName());
            if (databaseUser == null){
                user.setCreateDate(new Date());
            }
        }
        super.saveEntity(bi);
    }
}
