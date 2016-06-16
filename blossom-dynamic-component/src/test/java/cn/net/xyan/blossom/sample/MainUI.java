package cn.net.xyan.blossom.sample;

import cn.net.xyan.blossom.sample.view.AccessDeniedView;
import cn.net.xyan.blossom.sample.view.ErrorView;
import com.vaadin.annotations.Push;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.sidebar.security.VaadinSecurityItemFilter;


import com.vaadin.annotations.Theme;

import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.boot.internal.VaadinServletConfigurationProperties;

import org.springframework.context.ApplicationContext;

import org.vaadin.spring.security.util.SecurityExceptionUtils;
import org.vaadin.spring.sidebar.components.ValoSideBar;


/**
 * Created by zarra on 16/4/15.
 */
@SpringUI(path = "/")
@Theme("valo")
//@Push(transport = Transport.LONG_POLLING)
public class MainUI extends UI  {

    Logger logger = LoggerFactory.getLogger(MainUI.class);

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    VaadinSecurity vaadinSecurity;

    @Autowired
    SpringViewProvider springViewProvider;

    @Autowired
    ValoSideBar sideBar;


    @Autowired
    VaadinServletConfigurationProperties vaadinServletConfigurationProperties;

    @Override
    protected void init(VaadinRequest request) {

        getPage().setTitle("Dynamic Component");


        // Let's register a custom error handler to make the 'access denied' messages a bit friendlier.
        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                if (SecurityExceptionUtils.isAccessDeniedException(event.getThrowable())) {
                    Notification.show("Sorry, you don't have access to do that.");
                } else {
                    super.error(event);
                }
            }
        });
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        // By adding a security item filter, only views that are accessible to the user will show up in the side bar.
        sideBar.setItemFilter(new VaadinSecurityItemFilter(vaadinSecurity));
        layout.addComponent(sideBar);

        CssLayout viewContainer = new CssLayout();
        viewContainer.setSizeFull();
        layout.addComponent(viewContainer);
        layout.setExpandRatio(viewContainer, 1f);

        Navigator navigator = new Navigator(this, viewContainer);
        // Without an AccessDeniedView, the view provider would act like the restricted views did not exist at all.
        springViewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
        navigator.addProvider(springViewProvider);
        navigator.setErrorView(ErrorView.class);
        navigator.navigateTo(navigator.getState());

        setContent(layout); // Call this here because the Navigator must have been configured before the Side Bar can be attached to a UI.
    }



}
