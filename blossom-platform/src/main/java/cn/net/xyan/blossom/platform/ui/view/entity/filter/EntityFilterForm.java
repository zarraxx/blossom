package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/5.
 */
public class EntityFilterForm<E> extends VerticalLayout implements Button.ClickListener {

    EntityRenderConfiguration<E> renderConfiguration;

    List<UISpecification<E>> specifications = new LinkedList<>();

    AbstractOrderedLayout contentUI;

    JPAContainer<E> jpaContainer;

    public EntityFilterForm(EntityRenderConfiguration<E> renderConfiguration,JPAContainer<E> jpaContainer){
        this.renderConfiguration = renderConfiguration;
        this.jpaContainer = jpaContainer;

        specifications.addAll(renderConfiguration.getSpecifications());

        setMargin(true);
        setSpacing(true);
        setSizeFull();

        createFilterSpec();

        addComponent(contentUI);

        setExpandRatio(contentUI,1);
    }

    public AbstractOrderedLayout createFilterSpec(){
        contentUI = new VerticalLayout();

        contentUI.setSpacing(true);

        for (UISpecification<E> specification: specifications){
            Component c = specification.createUI(contentUI);
            contentUI.addComponent(c);
        }

        return contentUI;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

    }
}
