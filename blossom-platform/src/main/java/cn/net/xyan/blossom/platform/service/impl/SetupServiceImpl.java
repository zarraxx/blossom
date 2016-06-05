package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.service.Installer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.spring.security.shared.VaadinUrlAuthenticationSuccessHandler;

import javax.servlet.ServletContext;
import java.util.List;


/**
 * Created by zarra on 16/5/31.
 */
@Service
public class SetupServiceImpl implements InitializingBean {

    @Autowired
    ServletContext servletContext;

    @Autowired
    List<Installer> installers;

    @Override
    public void afterPropertiesSet() throws Exception {

        ApplicationContextUtils.setServletContext(servletContext);

        Byte[] bytes = new Byte[0];

        Class<?> cls = bytes.getClass();

        String name = cls.getName();

        for (Installer installer:installers){
            installer.beforeSetup();
        }
        for (Installer installer:installers){
            installer.doSetupPage();
        }
        for (Installer installer:installers){
            installer.doSetupCatalog();
        }
        for (Installer installer:installers){
            installer.doSetupModule();
        }
        for (Installer installer:installers){
            installer.afterSetup();
        }

    }


}
