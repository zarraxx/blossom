package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Module;
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
@SpringView(name = "interface.module")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "Module", order = 3)
@FontAwesomeIcon(FontAwesome.COG)
public class ModuleView extends EntityView<Module> {

    @Autowired
    I18NService i18NService;

    public ModuleView(){
        super("Module");
    }

    @Override
    public void saveEntity(EntityItem<Module> bi) {
        Module module = bi.getEntity();
        String key = Module.moduleMessageKey(module.getCode());

        I18NString string = i18NService.setupMessage(key,module.getCode());

        module.setTitle(string);

        super.saveEntity(bi);
    }
}
