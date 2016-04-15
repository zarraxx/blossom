package cn.net.xyan.spring.boot.ui.login;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.security.shared.VaadinSharedSecurity;

/**
 * Created by zarra on 16/4/14.
 */
public class Login extends CustomComponent {

    VaadinSharedSecurity vaadinSecurity;

    VaadinRequest request;

    public Login(VaadinSharedSecurity vaadinSharedSecurity,VaadinRequest request){
        this.vaadinSecurity = vaadinSharedSecurity;
        this.request = request;
    }

    private TextField userName;

    private PasswordField passwordField;

    private CheckBox rememberMe;

    private Button login;

    private Label loginFailedLabel;
    private Label loggedOutLabel;


    @Override
    public void attach() {



        FormLayout loginForm = new FormLayout();



        loginForm.setCaption("<h2>Please Login</h2>");
        loginForm.setCaptionAsHtml(true);

        loginForm.setSizeUndefined();

        userName = new TextField("Username");
        passwordField = new PasswordField("Password");
        rememberMe = new CheckBox("Remember me");
        login = new Button("Login");
        loginForm.addComponent(userName);
        loginForm.addComponent(passwordField);
        loginForm.addComponent(rememberMe);
        loginForm.addComponent(login);
        login.addStyleName(ValoTheme.BUTTON_PRIMARY);
        login.setDisableOnClick(true);
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                login();
            }
        });

        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setSpacing(true);
        loginLayout.setSizeUndefined();

        if (request.getParameter("logout") != null) {
            loggedOutLabel = new Label("You have been logged out!");
            loggedOutLabel.addStyleName(ValoTheme.LABEL_SUCCESS);
            loggedOutLabel.setSizeUndefined();
            loginLayout.addComponent(loggedOutLabel);
            loginLayout.setComponentAlignment(loggedOutLabel, Alignment.BOTTOM_CENTER);
        }

        loginLayout.addComponent(loginFailedLabel = new Label());
        loginLayout.setComponentAlignment(loginFailedLabel, Alignment.BOTTOM_CENTER);
        loginFailedLabel.setSizeUndefined();
        loginFailedLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        loginFailedLabel.setVisible(false);

        loginLayout.addComponent(loginForm);
        loginLayout.setComponentAlignment(loginForm, Alignment.TOP_CENTER);

        VerticalLayout rootLayout = new VerticalLayout(loginLayout);
        rootLayout.setSizeFull();
        rootLayout.setComponentAlignment(loginLayout, Alignment.MIDDLE_CENTER);

        rootLayout.addStyleName("login-form");

        setCompositionRoot(rootLayout);
        //setSizeFull();
    }


    private void login() {
        try {
            vaadinSecurity.login(userName.getValue(), passwordField.getValue(), rememberMe.getValue());
        } catch (AuthenticationException ex) {
            userName.focus();
            userName.selectAll();
            passwordField.setValue("");
            loginFailedLabel.setValue(String.format("Login failed: %s", ex.getMessage()));
            loginFailedLabel.setVisible(true);
            if (loggedOutLabel != null) {
                loggedOutLabel.setVisible(false);
            }
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LoggerFactory.getLogger(getClass()).error("Unexpected error while logging in", ex);
        } finally {
            login.setEnabled(true);
        }
    }

}
