package cn.net.xyan.blossom.platform.ui.action;

import cn.net.xyan.blossom.core.utils.RequestUtils;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.UI;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/8.
 */
@SpringComponent
@SideBarItem(sectionId = UISystemService.CatalogOperation, caption = "Admin")
@FontAwesomeIcon(FontAwesome.COG)
public class GoToAdminOperation implements Runnable {
    @Override
    public void run() {
        String appRoot = RequestUtils.appRootURL();
        UI.getCurrent().getPage().setLocation(String.format("%s/ui/admin",appRoot));
    }
}
