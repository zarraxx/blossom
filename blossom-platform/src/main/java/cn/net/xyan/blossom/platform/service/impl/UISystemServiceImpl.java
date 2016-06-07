package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.dao.CatalogDao;
import cn.net.xyan.blossom.platform.dao.UIModuleDao;
import cn.net.xyan.blossom.platform.dao.UIPageDao;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.service.SecurityService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.AdminUI;
import cn.net.xyan.blossom.platform.ui.ContentUI;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.transaction.annotation.Transactional;

import org.vaadin.spring.sidebar.annotation.SideBarItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public class UISystemServiceImpl extends InstallerAdaptor implements UISystemService {
    @Autowired
    UIPageDao pageDao;

    @Autowired
    CatalogDao catalogDao;

    @Autowired
    UIModuleDao moduleDao;

    @Autowired
    I18NService i18NService;

    @Autowired
    SecurityService securityService;

    @Override
    public List<UIPage> setupPages() {
        List<UIPage> pages = new LinkedList<>();

        List< Class<? extends ContentUI> > contentUIs = ApplicationContextUtils.beanTypesForType(ContentUI.class);

        for (Class<? extends ContentUI> uiClass : contentUIs) {
            SpringUI springUI = uiClass.getAnnotation(SpringUI.class);
            if (springUI != null) {
                String path = springUI.path();
                UIPage page = setupPage(path, uiClass);
                pages.add(page);
            }
        }
        return pages;
    }

    @Override
    public List<Module> setupModules() {
        List<Module> modules = new LinkedList<>();

         String[] beanNames = ApplicationContextUtils.beanNamesForAnnotation(SideBarItem.class);

        for (String beanName:beanNames) {
            Class<?> viewClass = ApplicationContextUtils.beanTypeForBeanName(beanName);
            SideBarItem sideBarItem = viewClass.getAnnotation(SideBarItem.class);

            String viewName = beanName;
            String catalogID = sideBarItem.sectionId();
            Catalog catalog = catalogByCode(catalogID);

            String title = sideBarItem.caption();

            SpringView springView = viewClass.getAnnotation(SpringView.class);
            if (springView != null && View.class.isAssignableFrom(viewClass)) {
                viewName = springView.name();
            }

            Module module = setupModule(beanName,viewName, title, viewClass, catalog);
            modules.add(module);
        }
        return modules;
    }

    @Override
    @Transactional
    public UIPage setupPage(String path, Class<? extends ContentUI> uiClass) {
        UIPage page = pageDao.findOne(path);
        if (page == null) {
            page = new UIPage();
            page.setCode(path);
            page.setUiClassName(uiClass.getName());
            page = pageDao.saveAndFlush(page);
        }
        return page;
    }

    @Override
    public Catalog setupCatalog(String code, String title, UIPage page) {
        return setupCatalog(code,title,page,null);
    }

    @Override
    public Module setupModule(String beanName,String viewName, String title, Class<?> viewClass, Catalog catalog) {
        return setupModule(beanName,viewName,title,viewClass,catalog,null);
    }

    @Override
    @Transactional
    public Catalog setupCatalog(String code, String title, UIPage page, Permission permission) {
        Catalog catalog = catalogDao.findOne(code);
        if (catalog == null) {
            catalog = new Catalog();
            catalog.setCode(code);
            String key = Catalog.catalogMessageKey(code);
            I18NString string = i18NService.setupMessage(key, title);
            catalog.setTitle(string);
            catalog.getUiPages().add(page);

            if (permission!=null){
                catalog.getEssentialPermission().add(permission);
            }
            catalog = catalogDao.saveAndFlush(catalog);

            page.getCatalogs().add(catalog);
            pageDao.saveAndFlush(page);

        }
        return catalog;
    }

    @Override
    @Transactional
    public Module setupModule(String beanName, String viewName, String title, Class<?> viewClass, Catalog catalog, Permission permission) {
        Module module = moduleDao.findOne(beanName);
        if (module == null) {
            module = new Module();
            module.setCode(beanName);
            module.setViewName(viewName);
            module.setViewClassName(viewClass.getName());
            String key = Module.moduleMessageKey(beanName);
            I18NString string = i18NService.setupMessage(key, title);
            module.setTitle(string);

            if (permission!=null)
                module.getEssentialPermission().add(permission);

            module = moduleDao.saveAndFlush(module);
            if (catalog != null) {
                module.getCatalogs().add(catalog);
                catalog.getModules().add(module);
                catalogDao.saveAndFlush(catalog);
            }
        }
        return module;
    }

    @Override
    public UIPage pageByClass(Class<? extends ContentUI> uiClass) {
        return pageDao.findByUiClassName(uiClass.getName());
    }

    @Override
    public Catalog catalogByCode(String code) {
        return catalogDao.findOne(code);
    }

    @Override
    @Transactional
    public void doSetupCatalog() {
        UIPage admingPage = pageByClass(AdminUI.class);

        Permission superPermission = securityService.setupPermission(SecurityService.PermissionAdmin,SecurityService.PermissionAdmin);

        Catalog security  = setupCatalog(UISystemService.CatalogSecurity, "Security", admingPage,superPermission);
        Catalog i18n      = setupCatalog(UISystemService.CatalogInterface, "Interface", admingPage,superPermission);
        Catalog operation = setupCatalog(UISystemService.CatalogOperation, "Operation", admingPage);

        Catalog debug     = setupCatalog(UISystemService.CatalogDebug,"Debug",admingPage,superPermission);

        Catalog system     = setupCatalog(UISystemService.CatalogSystem,"System",admingPage,superPermission);
    }

    @Override
    @Transactional
    public void doSetupModule() {
        setupModules();
    }

    @Override
    @Transactional
    public void doSetupPage() {
        setupPages();
    }
}
