package cn.net.xyan.blossom.platform.ui.component;

import com.vaadin.ui.UI;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.sidebar.SideBarItemDescriptor;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.SideBarUtils;

import java.util.Collection;

/**
 * Created by zarra on 16/5/30.
 */
public class BSideBarUtils extends SideBarUtils {
    public BSideBarUtils(ApplicationContext applicationContext, I18N i18n) {
        super(applicationContext, i18n);
    }

    @Override
    public Collection<SideBarItemDescriptor> getSideBarItems(SideBarSectionDescriptor descriptor) {
        return super.getSideBarItems(descriptor);
    }

    @Override
    public Collection<SideBarSectionDescriptor> getSideBarSections(Class<? extends UI> uiClass) {
        return super.getSideBarSections(uiClass);
    }
}
