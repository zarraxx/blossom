package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.User;
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
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "security.user")
@SideBarItem(sectionId = UISystemService.CatalogSecurity, caption = "User", order = 0)
@FontAwesomeIcon(FontAwesome.USER)
public class UserView  extends VerticalLayout implements View {

    JPAContainer<User> container;

    Table table;

    public UserView (){
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.user.caption","Manager user!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        table = new Table("entity");

        addComponent(table);

        setExpandRatio(table,1);
    }

    @Override
    public void attach() {
        super.attach();
        User user = new User();
        user.setLoginName("test");
        container = EntityContainerFactory.jpaContainer(User.class);
        container.addEntity(user);
        table.setContainerDataSource(container);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
