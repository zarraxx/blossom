package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/6.
 */
@SpringView(name = "interface.catalog")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "Catalog", order = 2)
@FontAwesomeIcon(FontAwesome.COG)
public class CatalogView extends EntityView<Catalog> {

    public CatalogView(){
        super("Catalog");
    }
}
