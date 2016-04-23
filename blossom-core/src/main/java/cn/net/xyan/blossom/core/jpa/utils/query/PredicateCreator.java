package cn.net.xyan.blossom.core.jpa.utils.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

/**
 * Created by zarra on 16/1/22.
 */
public interface PredicateCreator {

    void  configure(From<?, ?> from, CriteriaQuery<?> query, CriteriaBuilder builder);
    //Predicate predicateForProperty( Object model, String propertyName, Object value, QueryCondition queryCondition);
    Predicate predicateForCondition(QueryConditionModel<?> qcm);
}
