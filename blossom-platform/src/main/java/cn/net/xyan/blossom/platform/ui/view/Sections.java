package cn.net.xyan.blossom.platform.ui.view;

import cn.net.xyan.blossom.platform.service.UISystemService;
import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarSection;
import org.vaadin.spring.sidebar.annotation.SideBarSections;

/**
 * Created by zarra on 16/5/30.
 */
@Component
@SideBarSections({
        @SideBarSection(id = Sections.VIEWS, caption = "Views"),
        @SideBarSection(id = Sections.SECURITY, caption = "Security"),
        @SideBarSection(id = Sections.OPERATIONS, caption = "Operations")
})
public class Sections {

    public static final String VIEWS = "views";
    public static final String SECURITY = UISystemService.CatalogSecurity;
    public static final String OPERATIONS = "operations";
}
