package cn.net.xyan.spring.boot.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.security.VaadinSecurity;

/**
 * Created by zarra on 16/4/14.
 */
@SpringUI(path = "/")
public class MainUI extends UI {

    @Autowired
    private  VaadinSecurity vaadinSecurity;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        Label label = new Label("Hello world");

        Button bLogout = new Button("Logout");

        bLogout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                vaadinSecurity.logout();
            }
        });

        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setSpacing(true);
        loginLayout.setSizeUndefined();

        loginLayout.addComponent(label);
        loginLayout.setComponentAlignment(label, Alignment.TOP_CENTER);

        loginLayout.addComponent(bLogout);
        loginLayout.setComponentAlignment(bLogout, Alignment.MIDDLE_CENTER);



        VerticalLayout rootLayout = new VerticalLayout(loginLayout);
        rootLayout.setSizeFull();

        rootLayout.setComponentAlignment(loginLayout,Alignment.MIDDLE_CENTER);


        setContent(rootLayout);
        setSizeFull();
    }
}
