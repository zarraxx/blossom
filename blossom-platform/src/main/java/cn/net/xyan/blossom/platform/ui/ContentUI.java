package cn.net.xyan.blossom.platform.ui;

import cn.net.xyan.blossom.core.ui.BSideBar;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.service.PlatformInfoService;
import cn.net.xyan.blossom.platform.service.SecurityService;
import cn.net.xyan.blossom.platform.ui.view.AccessDeniedView;
import cn.net.xyan.blossom.platform.ui.view.ErrorView;
import com.vaadin.annotations.Push;
import com.vaadin.data.Item;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zarra on 16/5/13.
 */
@Push(transport = Transport.LONG_POLLING)
public abstract class ContentUI extends UI implements DisposableBean {

    @Autowired
    VaadinSecurity vaadinSecurity;

    @Autowired
    SpringViewProvider springViewProvider;

    @Autowired
    SecurityService securityService;

    @Autowired
    PlatformInfoService platformInfoService;

    @Autowired
    BSideBar sideBar;

    Label time;

    protected Logger logger = LoggerFactory.getLogger(ContentUI.class);

    protected static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    private ScheduledFuture<?> jobHandle;

    protected Runnable updateTimeJob = new Runnable() {
        public void run() {
            access(new Runnable() {
                @Override
                @SuppressWarnings("unchecked")
                public void run() {
                    Date now = new Date();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    time.setValue(sdf.format(now));
                }
            });
        }
    };

    @Override
    protected void init(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        User user = securityService.currentUser();



        VerticalLayout head = new VerticalLayout();

        VerticalLayout footer = new VerticalLayout();

        String logoString = "Blossom";

        Label title = new Label(logoString);

        title.setStyleName(ValoTheme.LABEL_H3);

        head.addComponent(title);

        head.setComponentAlignment(title,Alignment.TOP_CENTER);

        cn.net.xyan.blossom.platform.service.PlatformInfoService.ArtifactInfo artifactInfo
                = platformInfoService.platformArtifactInfo();

        if (artifactInfo!=null && artifactInfo.getVersion()!=null){
            Label version = new Label(artifactInfo.getVersion());
            head.addComponent(version);
            head.setComponentAlignment(version,Alignment.BOTTOM_CENTER);
        }


        //sideBar.setLogo(logo);
        Label username = new Label();
        if (user!=null) {
            if (user.getRealName() != null)
                username.setValue(user.getRealName());
            else
                username.setValue(user.getLoginName());
        }
        footer.addComponent(username);

        footer.setComponentAlignment(username,Alignment.BOTTOM_CENTER);

        time = new Label();

        Date now = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        time.setValue(sdf.format(now));

        footer.addComponent(time);

        sideBar.setHeader(head);
        sideBar.setFooter(footer);


        //sideBar.set

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

        jobHandle = executorService.scheduleWithFixedDelay(updateTimeJob, 500, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Canceling background job");
        jobHandle.cancel(true);
    }
}
