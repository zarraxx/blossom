package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/5.
 */
public class EntityFilterForm<E> extends VerticalLayout  {

    EntityRenderConfiguration<E> renderConfiguration;

    List<UISpecification<E>> specifications = new LinkedList<>();

    AbstractOrderedLayout contentUI;

    JPAContainer<E> jpaContainer;

    public EntityFilterForm(EntityRenderConfiguration<E> renderConfiguration,JPAContainer<E> jpaContainer){
        this.renderConfiguration = renderConfiguration;
        this.jpaContainer = jpaContainer;

        for (FilterConfig filterConfig : renderConfiguration.getSpecificationsConfig()) {
            specifications.add(renderConfiguration.createUISpecification(filterConfig));
        }

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

    public boolean inputOk(){
        for (UISpecification<E> specification : specifications){
            if (specification.inputOk() == false)
                return false;
        }
        return true;
    }

    public boolean isActive(){
        for (UISpecification<E> specification:specifications){
            if (specification.isActive())
                return true;
        }
        return false;
    }

    public Specifications<E> createSpecifications(){
        Specifications<E> w = Specifications.where(new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.equal(cb.literal(1),1);
            }
        });

        for (UISpecification uiSpecification:specifications){
            w =  w.and(uiSpecification);
        }

        return w;
    }
}
