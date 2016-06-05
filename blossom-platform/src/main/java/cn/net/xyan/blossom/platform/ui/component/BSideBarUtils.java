package cn.net.xyan.blossom.platform.ui.component;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.model.CatalogSideBarSectionDescriptor;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.ContentUI;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vaadin.spring.i18n.I18N;
import org.vaadin.spring.sidebar.SideBarItemDescriptor;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.SideBarUtils;

import java.util.*;

/**
 * Created by zarra on 16/5/30.
 */
public class BSideBarUtils extends SideBarUtils {

    public  class ViewItemDescriptor extends SideBarItemDescriptor.ViewItemDescriptor{

        String beanName;
        public ViewItemDescriptor(String beanName, ApplicationContext applicationContext) {
            super(beanName, applicationContext);
            this.beanName = beanName;
        }

        @Override
        public String getCaption() {
            String key = Module.moduleMessageKey(beanName);
            Locale locale = LocaleContextHolder.getLocale();
            return i18NService.i18nMessage(key,locale);
        }
    }

    public  class ActionItemDescriptor extends SideBarItemDescriptor.ActionItemDescriptor{

        String beanName;
        public ActionItemDescriptor(String beanName, ApplicationContext applicationContext) {
            super(beanName, applicationContext);
            this.beanName = beanName;
        }

        @Override
        public String getCaption() {
            String key = Module.moduleMessageKey(beanName);
            Locale locale = LocaleContextHolder.getLocale();
            return i18NService.i18nMessage(key,locale);
        }
    }


    ApplicationContext applicationContext;

    @Autowired
    UISystemService uiSystemService;

    @Autowired
    I18NService i18NService;

    public BSideBarUtils(ApplicationContext applicationContext, I18N i18n) {
        super(applicationContext, i18n);
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<SideBarItemDescriptor> getSideBarItems(SideBarSectionDescriptor descriptor) {
        if (descriptor instanceof  CatalogSideBarSectionDescriptor){
            CatalogSideBarSectionDescriptor catalogSideBarSectionDescriptor = (CatalogSideBarSectionDescriptor) descriptor;
            List<SideBarItemDescriptor> items  = new LinkedList<>();

            SortedSet<Module> modules = catalogSideBarSectionDescriptor.getCatalog().getModules();

            for (Module module:modules){
                String beanName = module.getCode();
                Class<?> beanType = ApplicationContextUtils.beanTypeForBeanName(beanName);
                if (View.class.isAssignableFrom(beanType)) {
                    items.add(new ViewItemDescriptor(beanName, applicationContext));
                }else if (Runnable.class.isAssignableFrom(beanType)){
                    items.add(new ActionItemDescriptor(beanName, applicationContext));
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
                SortedSet<Catalog> catalogs = page.getCatalogs();
                if (catalogs != null) {
                    int i = 0;
                    for (Catalog catalog:catalogs) {

                        result.add(CatalogSideBarSectionDescriptor.create(catalog, i));
                        i++;
                    }
                }
            }

            return result;
        }else{
            return super.getSideBarSections(uiClass);
        }
    }
}
