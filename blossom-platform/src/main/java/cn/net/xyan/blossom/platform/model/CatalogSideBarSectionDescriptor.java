package cn.net.xyan.blossom.platform.model;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.UIPage;
import com.vaadin.ui.UI;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.annotation.SideBarSection;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/1.
 */
public class CatalogSideBarSectionDescriptor extends SideBarSectionDescriptor {
    @SideBarSection(id = "VIEW", caption = "Views")
    private static class __Dummy{

    }

    Catalog catalog;

    int order = 0;

    private  CatalogSideBarSectionDescriptor(@Nonnull Catalog catalog, int order,SideBarSection sideBarSection) {
        super(sideBarSection, null);
        this.catalog = catalog;
        this.order = order;
    }

    public static CatalogSideBarSectionDescriptor create(@Nonnull Catalog catalog, int order){
        SideBarSection sideBarSection = __Dummy.class.getAnnotation(SideBarSection.class);
        return new CatalogSideBarSectionDescriptor(catalog,order,sideBarSection);
    }

    @Override
    public int compareTo(SideBarSectionDescriptor o) {
        return super.compareTo(o);
    }

    @Override
    public String getCaption() {
        return catalog.getTitle().value();
    }

    @Override
    public String getId() {
        return catalog.getCode();
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isAvailableFor(Class<? extends UI> uiClass) {
        String className = uiClass.getName();
        List<UIPage> pages = catalog.getUiPages();
        List<String> classNames = new LinkedList<>();
        for (UIPage page : pages) {
            classNames.add(page.getUiClassName());
        }
        return classNames.indexOf(className) >= 0;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
