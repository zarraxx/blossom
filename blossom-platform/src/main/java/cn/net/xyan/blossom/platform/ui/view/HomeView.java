package cn.net.xyan.blossom.platform.ui.view;

import cn.net.xyan.blossom.core.i18n.TR;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/5/30.
 */
@SpringView(name = "home")
@SideBarItem(sectionId = Sections.VIEWS, caption = "Home", order = 0)
@FontAwesomeIcon(FontAwesome.HOME)
public class HomeView extends VerticalLayout implements View {

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
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
