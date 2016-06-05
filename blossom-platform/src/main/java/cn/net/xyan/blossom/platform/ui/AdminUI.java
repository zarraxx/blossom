package cn.net.xyan.blossom.platform.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.spring.annotation.SpringUI;

/**
 * Created by zarra on 16/5/13.
 */
@SpringUI(path = "/admin")
@Theme("blossom")
@Widgetset("cn.net.xyan.blossom.platform.BlossomUI")
public class AdminUI extends ContentUI{
}
