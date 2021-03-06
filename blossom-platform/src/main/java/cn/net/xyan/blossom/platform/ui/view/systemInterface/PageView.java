package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/2.
 */
@SpringView(name = "interface.page")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "Page", order = 1)
@FontAwesomeIcon(FontAwesome.COG)
public class PageView  extends EntityView<UIPage> {


    public PageView(){
        super("Page");
    }

}
