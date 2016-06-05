package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by zarra on 16/6/5.
 */
public abstract class UISpecification<E> implements Specification<E> {

    boolean isActive;

    FieldGroup fieldGroup;

    UISpecification(){
        fieldGroup = new FieldGroup();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public void setFieldGroup(FieldGroup fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    public abstract Component createUI(AbstractOrderedLayout parent);

    public abstract Predicate generatePredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    public abstract boolean inputOk();

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (isActive)
            return generatePredicate(root,query,cb);
        else
            return cb.equal(cb.literal(1),1);
    }
}
