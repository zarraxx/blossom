package cn.net.xyan.blossom.core.ui;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.spring.sidebar.SideBarItemDescriptor;
import org.vaadin.spring.sidebar.SideBarSectionDescriptor;
import org.vaadin.spring.sidebar.SideBarUtils;
import org.vaadin.spring.sidebar.components.AbstractSideBar;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public class BSideBar extends AbstractSideBar<CssLayout> {

    private Layout headerLayout;

    private Layout footerLayout;

    private Component logo;
    private boolean largeIcons = false;

    public BSideBar(SideBarUtils sideBarUtils) {
        super(sideBarUtils);

        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        setSizeUndefined();
    }

    @Override
    protected CssLayout createCompositionRoot() {
        CssLayout layout = new CssLayout();
        layout.addStyleName(ValoTheme.MENU_PART);
        if (largeIcons) {
            layout.addStyleName(ValoTheme.MENU_PART_LARGE_ICONS);
        }
        layout.setWidth("200px");
        layout.setHeight("100%");
        if (logo != null) {
            layout.addComponent(logo);
        }
        if (headerLayout != null) {
            layout.addComponent(headerLayout);
        }
//        if (footerLayout!=null){
//            layout.addComponent(footerLayout);
//        }
        return layout;
    }

    /**
     * Returns whether the side bar is using large icons or not. Default is false.
     *
     * @see ValoTheme#MENU_PART_LARGE_ICONS
     */
    public boolean isLargeIcons() {
        return largeIcons;
    }

    /**
     * Specifies whether the side bar should use large icons or not.
     *
     * @see ValoTheme#MENU_PART_LARGE_ICONS
     */
    public void setLargeIcons(boolean largeIcons) {
        this.largeIcons = largeIcons;
        if (getCompositionRoot() != null) {
            if (largeIcons) {
                getCompositionRoot().addStyleName(ValoTheme.MENU_PART_LARGE_ICONS);
            } else {
                getCompositionRoot().removeStyleName(ValoTheme.MENU_PART_LARGE_ICONS);
            }
        }
    }

    /**
     * Adds a header to the top of the side bar, below the logo. The {@link ValoTheme#MENU_TITLE} style
     * will automatically be added to the layout.
     *
     * @param headerLayout the layout containing the header, or {@code null} to remove.
     */
    public void setHeader(Layout headerLayout) {
        if (getCompositionRoot() != null && this.headerLayout != null) {
            getCompositionRoot().removeComponent(this.headerLayout);
        }
        this.headerLayout = headerLayout;
        if (headerLayout != null) {
            headerLayout.addStyleName(ValoTheme.MENU_TITLE);
            if (getCompositionRoot() != null) {
                if (this.logo != null) {
                    getCompositionRoot().addComponent(headerLayout, 1);
                } else {
                    getCompositionRoot().addComponentAsFirst(headerLayout);
                }
            }
        }
    }

    public void setFooter(Layout footerLayout){
        if (getCompositionRoot() != null && this.footerLayout != null) {
            getCompositionRoot().removeComponent(this.footerLayout);
        }
        this.footerLayout = footerLayout;
        if (footerLayout!=null){
            footerLayout.addStyleName("side-bar-footer");
        }


    }

    /**
     * Returns the header layout, or {@code null} if none has been set.
     *
     * @see #setHeader(com.vaadin.ui.Layout)
     */
    public Layout getHeader() {
        return headerLayout;
    }

    /**
     * Adds a logo to the very top of the side bar, above the header. The logo's primary style is automatically
     * set to {@link ValoTheme#MENU_LOGO} ands its size to undefined.
     *
     * @param logo a {@link com.vaadin.ui.Label} or {@link com.vaadin.ui.Button} to use as the logo, or {@code null} to remove the logo completely.
     */
    public void setLogo(Component logo) {
        if (getCompositionRoot() != null && this.logo != null) {
            getCompositionRoot().removeComponent(this.logo);
        }
        this.logo = logo;
        if (logo != null) {
            logo.setPrimaryStyleName(ValoTheme.MENU_LOGO);
            logo.setSizeUndefined();
            if (getCompositionRoot() != null) {
                getCompositionRoot().addComponentAsFirst(logo);
            }
        }
    }

    /**
     * Returns the logo, or {@code null} if none has been set.
     *
     * @see #setLogo(com.vaadin.ui.Component)
     */
    public Component getLogo() {
        return logo;
    }

    @Override
    protected SectionComponentFactory<CssLayout> createDefaultSectionComponentFactory() {
        return new DefaultSectionComponentFactory();
    }

    @Override
    protected ItemComponentFactory createDefaultItemComponentFactory() {
        return new DefaultItemComponentFactory();
    }

    /**
     * Extended version of {@link com.vaadin.ui.Button} that is used by the {@link BSideBar.DefaultItemComponentFactory}.
     */
    static class ItemButton extends Button {

        ItemButton(final SideBarItemDescriptor descriptor) {
            setPrimaryStyleName(ValoTheme.MENU_ITEM);
            setCaption(descriptor.getCaption());
            setIcon(descriptor.getIcon());
            setId(descriptor.getItemId());
            setDisableOnClick(true);
            addClickListener(new Button.ClickListener() {

                private static final long serialVersionUID = -8512905888847432801L;

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        descriptor.itemInvoked(getUI());
                    } finally {
                        setEnabled(true);
                    }
                }

            });
        }
    }

    /**
     * Extended version of {@link BSideBar.ItemButton} that is used for view items. This
     * button keeps track of the currently selected view in the current UI's {@link com.vaadin.navigator.Navigator} and
     * updates its style so that the button of the currently visible view can be highlighted.
     */
    static class ViewItemButton extends ItemButton implements ViewChangeListener {

        private final String viewName;
        private static final String STYLE_SELECTED = "selected";

        ViewItemButton(SideBarItemDescriptor.ViewItemDescriptor descriptor) {
            super(descriptor);
            viewName = descriptor.getViewName();
        }

        @Override
        public void attach() {
            super.attach();
            if (getUI().getNavigator() == null) {
                throw new IllegalStateException("Please configure the Navigator before you attach the SideBar to the UI");
            }
            getUI().getNavigator().addViewChangeListener(this);
        }

        @Override
        public void detach() {
            getUI().getNavigator().removeViewChangeListener(this);
            super.detach();
        }

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            if (event.getViewName().equals(viewName)) {
                addStyleName(STYLE_SELECTED);
            } else {
                removeStyleName(STYLE_SELECTED);
            }
        }
    }

    /**
     * Default implementation of {@link BSideBar.SectionComponentFactory} that adds the section header
     * and items directly to the composition root.
     */
    public class DefaultSectionComponentFactory implements SectionComponentFactory<CssLayout> {

        private ItemComponentFactory itemComponentFactory;

        @Override
        public void setItemComponentFactory(ItemComponentFactory itemComponentFactory) {
            this.itemComponentFactory = itemComponentFactory;
        }

        @Override
        public void createSection(CssLayout compositionRoot, SideBarSectionDescriptor descriptor, Collection<SideBarItemDescriptor> itemDescriptors) {
            Button header = new Button();
            header.setCaption(descriptor.getCaption());
            header.setSizeUndefined();
            header.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
            compositionRoot.addComponent(header);
            List<Component> components = new LinkedList<>();
            for (SideBarItemDescriptor item : itemDescriptors) {
                Component c = itemComponentFactory.createItemComponent(item);
                c.setVisible(false);
                compositionRoot.addComponent(c);
                components.add(c);
            }
            header.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    for (Component c : components){
                        c.setVisible(!c.isVisible());
                    }
                }
            });
        }
    }

    /**
     * Default implementation of {@link BSideBar.ItemComponentFactory} that creates
     * {@link com.vaadin.ui.Button}s.
     */
    public class DefaultItemComponentFactory implements ItemComponentFactory {

        @Override
        public Component createItemComponent(SideBarItemDescriptor descriptor) {
            if (descriptor instanceof SideBarItemDescriptor.ViewItemDescriptor) {
                return new ViewItemButton((SideBarItemDescriptor.ViewItemDescriptor) descriptor);
            } else {
                return new ItemButton(descriptor);
            }
        }
    }

    @Override
    public void attach() {
        super.attach();
        if (footerLayout!=null){
            getCompositionRoot().addComponent(footerLayout);
        }
    }
}
