package cn.net.xyan.blossom.platform.ui.action;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.ui.view.Sections;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/5/30.
 */
@SpringComponent
@SideBarItem(sectionId = Sections.OPERATIONS, captionCode = TR.Logout)
@FontAwesomeIcon(FontAwesome.POWER_OFF)
public class LogoutOperation implements Runnable {

    private final VaadinSecurity vaadinSecurity;

    @Autowired
    public LogoutOperation(VaadinSecurity vaadinSecurity) {
        this.vaadinSecurity = vaadinSecurity;
    }

    @Override
    public void run() {
        vaadinSecurity.logout();
    }
}
