package cn.net.xyan.blossom.platform.ui;

import cn.net.xyan.blossom.core.i18n.TR;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.vaadin.spring.security.shared.VaadinSharedSecurity;
import org.vaadin.spring.security.shared.VaadinUrlAuthenticationSuccessHandler;

import java.util.Locale;

/**
 * Created by zarra on 16/5/13.
 */
@SpringUI(path = "/login")
@Theme("blossom")
@Widgetset("cn.net.xyan.blossom.platform.BlossomUI")
public class LoginUI extends UI {


    Logger logger = LoggerFactory.getLogger(LoginUI.class);

    float inputFieldWidth = 300f;

    @Autowired
    VaadinSharedSecurity vaadinSecurity;

    @Autowired
    VaadinUrlAuthenticationSuccessHandler authenticationSuccessHandler;

    private TextField userName;

    private PasswordField passwordField;

    private ComboBox cbLocale;

    private CheckBox rememberMe;

    private Button login;

    private Label loginFailedLabel;
    private Label loggedOutLabel;

    @Override
    protected void init(VaadinRequest request) {

        String refer = request.getHeader("Referer");
        Locale locale = LocaleContextHolder.getLocale();

        //UI.getCurrent().setLocale(locale);

        getPage().setTitle(TR.m("ui.login.title","Login"));

        FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");

        loginForm.setCaption(TR.m("ui.login.caption","Please Login!"));
        loginForm.setSizeUndefined();

        userName = new TextField(TR.m("ui.login.username","Username"));
        userName.setWidth(inputFieldWidth,Unit.PIXELS);
        passwordField = new PasswordField(TR.m("ui.login.password","Password"));
        passwordField.setWidth(inputFieldWidth,Unit.PIXELS);
        rememberMe = new CheckBox(TR.m("ui.login.rememberMe","Remember me"));
        login = new Button(TR.m(TR.Login,"Login"));

        cbLocale = new ComboBox(TR.m("ui.login.locale","Language"));
        cbLocale.setWidth(inputFieldWidth,Unit.PIXELS);
        Locale enUS = Locale.US;
        Locale zhCN = Locale.SIMPLIFIED_CHINESE;

        logger.info(zhCN.getDisplayName(enUS));

        cbLocale.addItem(enUS);
        cbLocale.setItemCaption(enUS,TR.localeDisplayName(enUS));
        cbLocale.addItem(zhCN);
        cbLocale.setItemCaption(zhCN,TR.localeDisplayName(zhCN));
        cbLocale.setNullSelectionAllowed(false);

        if (zhCN.getLanguage().equals(locale.getLanguage())){
            cbLocale.setValue(zhCN);
        }else{
            cbLocale.setValue(enUS);
        }


        loginForm.addComponent(userName);
        loginForm.addComponent(passwordField);
        loginForm.addComponent(cbLocale);
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
        setContent(rootLayout);
        setSizeFull();
    }

    private void login() {
       //authenticationSuccessHandler.setUseReferer(true);
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
