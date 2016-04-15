package cn.net.xyan.spring.boot.ui.login;

import cn.net.xyan.spring.boot.ui.login.Login;
import com.vaadin.annotations.Theme;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.security.shared.VaadinSharedSecurity;

/**
 * Created by zarra on 16/4/14.
 */
@SpringUI(path = "/login")
@Theme("xyan")
public class LoginUI extends UI {
    @Autowired
    VaadinSharedSecurity vaadinSecurity;

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("Login");

        Login sample = new Login(vaadinSecurity,request);


        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setSpacing(true);
        loginLayout.setSizeUndefined();

        loginLayout.addComponent(sample);
        loginLayout.setComponentAlignment(sample,Alignment.TOP_CENTER);


        VerticalLayout rootLayout = new VerticalLayout(loginLayout);
        rootLayout.setSizeFull();

        rootLayout.setComponentAlignment(loginLayout,Alignment.MIDDLE_CENTER);


        setContent(rootLayout);
        setSizeFull();
    }


}
