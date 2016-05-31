package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.ui.ContentUI;

import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public interface UISystemService {
    String CatalogSecurity = "security";
    String CatalogI18n = "i18n";

    UIPage setupPage(String path, Class<? extends ContentUI> uiClass);

    Catalog setupCatalog(String code, String title, UIPage page);

    Module setupModule(String viewName, String title, Class<? extends com.vaadin.navigator.View> viewClass, Catalog catalog);

    UIPage pageByClass(Class<? extends ContentUI> uiClass);

    Catalog catalogByCode(String code);

    List<String> getScanPackages();

    void setScanPackages(List<String> scanPackages);


    void setup();
}
