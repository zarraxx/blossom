package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.UIPage_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditFrom;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

import java.util.Map;
import java.util.SortedSet;

/**
 * Created by zarra on 16/6/4.
 */
public class PageConfig extends EntityRenderConfiguration<UIPage> {

    @Override
    public void configTableColumnHeader() {
        addTableColumn(UIPage_.code).setDisplayTitle("Code");
        addTableColumn(UIPage_.title).setDisplayTitle("Title");
        addTableColumn(UIPage_.uiClassName).setDisplayTitle("UiClass");

        addTableColumn(UIPage_.catalogs).setDisplayTitle("Catalogs");

        addTableColumn(UIPage_.sortOrder).setDisplayTitle("SortOrder");
    }

    @Override
    public void configFormFiled() {
        addFormField(UIPage_.code);
        addFormField(UIPage_.title);
        addFormField(UIPage_.uiClassName);
        addFormField(UIPage_.sortOrder);
        addFormField(UIPage_.catalogs);
    }
}
