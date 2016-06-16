package cn.net.xyan.blossom.sample;

import org.springframework.stereotype.Component;
import org.vaadin.spring.sidebar.annotation.SideBarSection;
import org.vaadin.spring.sidebar.annotation.SideBarSections;

/**
 * Created by zarra on 16/6/10.
 */
@Component
@SideBarSections({
        @SideBarSection(id = Sections.VIEWS, caption = "Views"),
        @SideBarSection(id = Sections.OPERATIONS, caption = "Operations")
})
public class Sections {

    public static final String VIEWS = "views";
    public static final String OPERATIONS = "operations";
}

