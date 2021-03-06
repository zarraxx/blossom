package cn.net.xyan.blossom.platform.ui.component;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.*;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.model.CatalogSideBarSectionDescriptor;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.SecurityService;
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

        String viewName;
        String param;
        ApplicationContext applicationContext;

        public ViewItemDescriptor(VaadinViewModule module,ApplicationContext applicationContext){
            super(module.getViewBeanName(),applicationContext);
            this.viewName = module.getViewName();
            this.param = module.getViewParameter();
            this.applicationContext = applicationContext;
        }

        @Override
        public String getCaption() {
            String key = Module.moduleMessageKey(viewName);
            Locale locale = LocaleContextHolder.getLocale();
            return i18NService.i18nMessage(key,locale);
        }

        @Override
        public String getViewName() {
            return viewName;
        }

        @Override
        public void itemInvoked(UI ui) {
            if (param!=null)
                ui.getNavigator().navigateTo(viewName+"/"+param);
            else
                ui.getNavigator().navigateTo(viewName);
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

    @Autowired
    SecurityService securityService;

    public BSideBarUtils(ApplicationContext applicationContext, I18N i18n) {
        super(applicationContext, i18n);
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<SideBarItemDescriptor> getSideBarItems(SideBarSectionDescriptor descriptor) {
        if (descriptor instanceof  CatalogSideBarSectionDescriptor){
            CatalogSideBarSectionDescriptor catalogSideBarSectionDescriptor = (CatalogSideBarSectionDescriptor) descriptor;
            List<SideBarItemDescriptor> items  = new LinkedList<>();

            User user = securityService.currentUser();
            Catalog catalog = catalogSideBarSectionDescriptor.getCatalog();
            Collection<Module> modules = securityService.modulePermitInCatalogForUser(catalog,user);


            for (Module module:modules){

                String beanName = null;

                if (module instanceof VaadinViewModule){
                    VaadinViewModule viewModule = (VaadinViewModule) module;
                    beanName = viewModule.getViewBeanName();
                }else if (module instanceof OperationModule){
                    OperationModule operationModule = (OperationModule) module;
                    beanName = operationModule.getBeanName();
                }

                Class<?> beanType = ApplicationContextUtils.beanTypeForBeanName(beanName);

//                List<Permission> permissionList = new LinkedList<>();
//                permissionList.addAll(module.getEssentialPermission());
//                if (permissionList.size()>0){
//                    if (!securityService.checkPermissionForUser(user,permissionList)){
//                        continue;
//                    }
//                }


                if (View.class.isAssignableFrom(beanType)) {
                    items.add(new ViewItemDescriptor((VaadinViewModule) module,applicationContext));
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

        User user = securityService.currentUser();


        if (ContentUI.class.isAssignableFrom(uiClass)) {

            List<SideBarSectionDescriptor> result = new LinkedList<>();

            UIPage page = uiSystemService.pageByClass((Class<? extends ContentUI>) uiClass);

            if (page != null) {
                Collection<Catalog> catalogs = securityService.catalogsPermitInPageForUser(page,user);
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
