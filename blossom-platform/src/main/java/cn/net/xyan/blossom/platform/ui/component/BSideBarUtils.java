package cn.net.xyan.blossom.platform.ui.component;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.model.CatalogSideBarSectionDescriptor;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.ContentUI;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.sidebar.SideBarItemDescriptor;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.SideBarUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public class BSideBarUtils extends SideBarUtils {

    ApplicationContext applicationContext;

    @Autowired
    UISystemService uiSystemService;

    public BSideBarUtils(ApplicationContext applicationContext, I18N i18n) {
        super(applicationContext, i18n);
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<SideBarItemDescriptor> getSideBarItems(SideBarSectionDescriptor descriptor) {
        if (descriptor instanceof  CatalogSideBarSectionDescriptor){
            CatalogSideBarSectionDescriptor catalogSideBarSectionDescriptor = (CatalogSideBarSectionDescriptor) descriptor;
            List<SideBarItemDescriptor> items  = new LinkedList<>();

            List<Module> modules = catalogSideBarSectionDescriptor.getCatalog().getModules();

            for (Module module:modules){
                String beanName = module.getCode();
                Class<?> beanType = ApplicationContextUtils.beanTypeForBeanName(beanName);
                if (View.class.isAssignableFrom(beanType)) {
                    items.add(new SideBarItemDescriptor.ViewItemDescriptor(beanName, applicationContext));
                }else if (Runnable.class.isAssignableFrom(beanType)){
                    items.add(new SideBarItemDescriptor.ActionItemDescriptor(beanName, applicationContext));
                }
            }

            return items;

        }else {
            return super.getSideBarItems(descriptor);
        }
    }

    @Override
    public Collection<SideBarSectionDescriptor> getSideBarSections(Class<? extends UI> uiClass) {

        if (ContentUI.class.isAssignableFrom(uiClass)) {

            List<SideBarSectionDescriptor> result = new LinkedList<>();

            UIPage page = uiSystemService.pageByClass((Class<? extends ContentUI>) uiClass);

            if (page != null) {
                List<Catalog> catalogs = page.getCatalogs();
                if (catalogs != null) {
                    for (int i = 0; i < catalogs.size(); i++) {
                        Catalog catalog = catalogs.get(i);
                        result.add(CatalogSideBarSectionDescriptor.create(catalog, i));
                    }
                }
            }

            return result;
        }else{
            return super.getSideBarSections(uiClass);
        }
    }
}
