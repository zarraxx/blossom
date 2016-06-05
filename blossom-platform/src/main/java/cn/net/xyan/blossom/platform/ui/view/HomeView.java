package cn.net.xyan.blossom.platform.ui.view;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.ContentUI;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "")
@SideBarItem(sectionId = Sections.VIEWS, caption = "Home", order = 0)
@FontAwesomeIcon(FontAwesome.HOME)
public class HomeView extends VerticalLayout implements View {

    @Autowired
    UISystemService uiSystemService;

    Logger logger = LoggerFactory.getLogger(HomeView.class);

    public HomeView() {
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.home.caption","Welcome to the System!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        Label body = new Label(TR.m("view.home.content","Welcome to the system"));
        body.setContentMode(ContentMode.HTML);
        addComponent(body);
    }

    @Override
    public void attach() {
        super.attach();
    }

    public Class<? extends UI> currentUIClass(){
        UI ui = UI.getCurrent();
        return ui.getClass();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Class<? extends UI> uiClass = currentUIClass();
        if (ContentUI.class.isAssignableFrom(uiClass)){
            Class<? extends ContentUI> contentUIClass = (Class<? extends ContentUI>) uiClass;
            UIPage page = uiSystemService.pageByClass(contentUIClass);
            if (page.getCatalogs().size()>0){
                Catalog catalog = page.getCatalogs().first();
                if (catalog.getModules().size()>0){
                    Module module = catalog.getModules().first();
                    String viewName = module.getViewName();
                    try {
                        Class<?> cls = Class.forName(module.getViewClassName());
                        if (View.class.isAssignableFrom(cls)){
                            event.getNavigator().navigateTo(viewName);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new StatusAndMessageError(-9,e);
                    }
                }
            }
            logger.info(page.getCode());
        }
    }
}
