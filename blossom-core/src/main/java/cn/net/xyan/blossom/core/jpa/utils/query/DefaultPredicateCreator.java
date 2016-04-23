package cn.net.xyan.blossom.core.jpa.utils.query;

import cn.net.xyan.blossom.core.jpa.utils.JPA;

import javax.persistence.criteria.*;
import java.util.Collection;

/**
 * Created by zarra on 16/1/22.
 */
public class DefaultPredicateCreator implements PredicateCreator {

    From<?,?> from;
    CriteriaQuery<?> query;
    CriteriaBuilder builder;

    @Override
    public void configure(From<?, ?> from, CriteriaQuery<?> query, CriteriaBuilder builder) {
        this.from = from;
        this.query = query;
        this.builder = builder;
    }

    //@Override
//    public Predicate predicateForProperty(Object model, String propertyName, Object propertyValue, QueryCondition queryCondition) {
//
//        JPA.Operator operator = queryCondition.operator();
//
//        propertyName = Query.propertyName(propertyName);
//
//        //是否忽略null值
//        if (propertyValue == null ) {
//            if (queryCondition.ignoreNull())
//                return null;
//            else{
//                Expression<Object> path = from.get(propertyName);
//                return JPA.simpleLogic(operator,path,null,builder);
//            }
//        }
//
//        Class<?> valueClass = propertyValue.getClass();
//
//        if (queryCondition.hasIgnoreValue() && queryCondition.ignoreValue()!=null&& queryCondition.ignoreValue().length()>0){
//            if (queryCondition.ignoreValue().equals(propertyValue.toString())){
//                return null;
//            }
//        }
//        if (Collection.class.isAssignableFrom(valueClass)){
//            if (operator == JPA.Operator.Equal)
//                operator = JPA.Operator.In;
//            Collection<Object> collection = (Collection<Object>) propertyValue;
//            Expression<Object> expression = from.get(propertyName);
//            return JPA.collectionLogic(operator,expression,collection,builder);
//        }else if (String.class.isAssignableFrom(valueClass)){
//            String string = (String) propertyValue;
//            Expression<String> stringPath = from.get(propertyName);
//            return JPA.stringLogic(operator,stringPath,string,builder);
//        }else if(Comparable.class.isAssignableFrom(valueClass)){
//            Comparable<?> comparable = (Comparable<?>) propertyValue;
//            Expression<Comparable> path = from.get(propertyName);
//            return JPA.comparableLogic(operator,path,comparable,builder);
//        }else{
//            Expression<Object> path = from.get(propertyName);
//            return JPA.simpleLogic(operator,path, propertyValue,builder);
//        }
//    }

    @Override
    public Predicate predicateForCondition(QueryConditionModel<?> qcm) {
        JPA.Operator operator = qcm.getOperator();

        String propertyName = Query.propertyName(qcm.getPath());

        //是否忽略null值
        if (qcm.getValue() == null ) {
            if (qcm.isIgnoreNull())
                return null;
            else{
                Expression<Object> path = from.get(propertyName);
                return JPA.simpleLogic(operator,path,null,builder);
            }
        }

        Class<?> valueClass = qcm.getValue().getClass();

        if (qcm.isHasIgnoreValue() && qcm.getIgnoreValue()!=null&& qcm.getIgnoreValue().length()>0){
            if (qcm.getIgnoreValue().equals(qcm.getValue().toString())){
                return null;
            }
        }
        if (Collection.class.isAssignableFrom(valueClass)){
            if (operator == JPA.Operator.Equal)
                operator = JPA.Operator.In;
            Collection<Object> collection = (Collection<Object>) qcm.getValue();
            Expression<Object> expression = from.get(propertyName);
            return JPA.collectionLogic(operator,expression,collection,builder);
        }else if (String.class.isAssignableFrom(valueClass)){
            String string = (String) qcm.getValue();
            Expression<String> stringPath = from.get(propertyName);
            return JPA.stringLogic(operator,stringPath,string,builder);
        }else if(Comparable.class.isAssignableFrom(valueClass)){
            Comparable<?> comparable = (Comparable<?>) qcm.getValue();
            Expression<Comparable> path = from.get(propertyName);
            return JPA.comparableLogic(operator,path,comparable,builder);
        }else{
            Expression<Object> path = from.get(propertyName);
            return JPA.simpleLogic(operator,path, qcm.getValue(),builder);
        }
    }
}
