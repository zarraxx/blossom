package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/6.
 */
@SpringView(name = "interface.module")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "Module", order = 3)
@FontAwesomeIcon(FontAwesome.COG)
public class ModuleView extends EntityView<Module> {
    public ModuleView(){
        super("Module");
    }
}
