package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.dao.CatalogDao;
import cn.net.xyan.blossom.platform.dao.UIModuleDao;
import cn.net.xyan.blossom.platform.dao.UIPageDao;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.AdminUI;
import cn.net.xyan.blossom.platform.ui.ContentUI;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by zarra on 16/5/30.
 */
public class UISystemServiceImpl implements UISystemService, InitializingBean {
    @Autowired
    UIPageDao pageDao;

    @Autowired
    CatalogDao catalogDao;

    @Autowired
    UIModuleDao moduleDao;

    @Autowired
    I18NService i18NService;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    List<String> scanPackages;

    public List<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    @Transactional
    public void deleteAllPage() {
        pageDao.deleteAll();
        //throw  new StatusAndMessageError(-1,"error");
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
    @Transactional
    public Catalog setupCatalog(String code, String title, UIPage page) {
        Catalog catalog = catalogDao.findOne(code);
        if (catalog == null) {
            catalog = new Catalog();
            catalog.setCode(code);
            String key = String.format("ui.catalog.%s.title", code);
            I18NString string = i18NService.setupMessage(key, title);
            catalog.setTitle(string);
            catalog.getUiPages().add(page);
            catalog = catalogDao.saveAndFlush(catalog);

            page.getCatalogs().add(catalog);
            pageDao.saveAndFlush(page);

        }
        return catalog;
    }

    @Override
    @Transactional
    public Module setupModule(String viewName, String title, Class<? extends View> viewClass, Catalog catalog) {
        Module module = moduleDao.findOne(viewName);
        if (module == null) {
            module = new Module();
            module.setCode(viewName);
            module.setViewName(viewName);
            module.setViewClassName(viewClass.getName());
            String key = String.format("ui.module.%s.title", viewName);
            I18NString string = i18NService.setupMessage(key, title);
            module.setTitle(string);
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

    @Transactional
    public void setup(){
        List<String> packages = Arrays.asList("cn.net.xyan.blossom");

        if (getScanPackages()!=null)
            packages.addAll(getScanPackages());

        Set< Class<? extends ContentUI>> contentUIs = ReflectUtils.scanPackages(ContentUI.class,packages.toArray(new String[0]));

        Set< Class<? extends View>> views = ReflectUtils.scanPackages(View.class,packages.toArray(new String[0]));


        for (Class<? extends ContentUI> uiClass : contentUIs) {
            SpringUI springUI = uiClass.getAnnotation(SpringUI.class);
            if (springUI != null) {
                String path = springUI.path();
                setupPage(path, uiClass);
            }
        }

        String adminClass = AdminUI.class.getName();
        UIPage admingPage = pageDao.findByUiClassName(adminClass);

        Catalog security = setupCatalog(CatalogSecurity, "Security", admingPage);
        Catalog i18n = setupCatalog(CatalogI18n, "Internationalization", admingPage);

        for (Class<? extends View> viewClass : views) {
            SpringView springView = viewClass.getAnnotation(SpringView.class);
            SideBarItem sideBarItem = viewClass.getAnnotation(SideBarItem.class);
            if (springView != null && sideBarItem != null) {
                String viewName = springView.name();
                String catalogID = sideBarItem.sectionId();
                Catalog catalog = catalogByCode(catalogID);

                String title = sideBarItem.caption();
                setupModule(viewName, title, viewClass, catalog);

            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        try {
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }catch (Throwable e){

        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();//事务定义类
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            setup();
        }catch (Throwable throwable){
            transactionManager.rollback(status);
            throw  throwable;
        } finally{
            EntityManagerHolder emHolder = (EntityManagerHolder)
                    TransactionSynchronizationManager.unbindResource(entityManagerFactory);
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        }


    }
}
