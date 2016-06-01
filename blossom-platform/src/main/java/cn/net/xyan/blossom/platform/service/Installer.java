package cn.net.xyan.blossom.platform.service;

/**
 * Created by zarra on 16/6/1.
 */
public interface Installer {

    void doSetupPage();
    void doSetupCatalog();
    void doSetupModule();

    void beforeSetup();
    void afterSetup();
}
