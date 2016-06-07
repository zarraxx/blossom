package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
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
 * Created by zarra on 16/6/6.
 */
@SpringView(name = "interface.catalog")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "Catalog", order = 2)
@FontAwesomeIcon(FontAwesome.COG)
public class CatalogView extends EntityView<Catalog> {

    @Autowired
    I18NService i18NService;

    public CatalogView(){
        super("Catalog");
    }

    @Override
    public void saveEntity(EntityItem<Catalog> bi) {
        Catalog catalog = bi.getEntity();
        String key = Catalog.catalogMessageKey(catalog.getCode());
        I18NString string = i18NService.setupMessage(key,catalog.getCode());
        catalog.setTitle(string);
        super.saveEntity(bi);
    }
}
