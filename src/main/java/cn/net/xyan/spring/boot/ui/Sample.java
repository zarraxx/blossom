package cn.net.xyan.spring.boot.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.*;

/**
 * Created by zarra on 16/4/14.
 */
@Theme("xyan")
public class Sample extends CustomComponent{
    @Override
    public void attach() {
        super.attach();
        Label sample = new Label("abc123");
        Label l2 = new Label("next");



        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.setSizeUndefined();

        verticalLayout.addComponent(sample);
        verticalLayout.addComponent(l2);

        verticalLayout.addStyleName("test");

        setCompositionRoot(verticalLayout);


    }
}
