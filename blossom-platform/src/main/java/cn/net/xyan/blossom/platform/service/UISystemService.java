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
    String CatalogInterface = "interface";
    String CatalogOperation = "operation";

    UIPage setupPage(String path, Class<? extends ContentUI> uiClass);

    Catalog setupCatalog(String code, String title, UIPage page);

    Module setupModule(String beanName,String viewName, String title, Class<?> viewClass, Catalog catalog);

    UIPage pageByClass(Class<? extends ContentUI> uiClass);

    Catalog catalogByCode(String code);

    List<UIPage> setupPages();

    List<Module> setupModules();

}
