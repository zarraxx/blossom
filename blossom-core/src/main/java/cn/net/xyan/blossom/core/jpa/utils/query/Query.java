package cn.net.xyan.blossom.core.jpa.utils.query;


import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.jpa.annotation.QueryCondition;
import cn.net.xyan.blossom.core.jpa.annotation.QueryRequest;
import cn.net.xyan.blossom.core.jpa.annotation.ResultColumn;
import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zarra on 16/1/22.
 */
public class Query {

    static public String joinName(String propertyName) {
        if (propertyName.indexOf(".") < 0) {
            return "";
        } else {
            int lastIndex = propertyName.lastIndexOf(".");
            String joinName = propertyName.substring(0, lastIndex);
            return joinName;
        }
    }

    static public String propertyName(String propertyName) {
        if (propertyName.indexOf(".") < 0) {
            return propertyName;
        } else {
            int lastIndex = propertyName.lastIndexOf(".");
            String result = propertyName.substring(lastIndex + 1, propertyName.length());
            return result;
        }
    }

    static public <T> T tuple2Object(Tuple tuple, List<String> properties, Class<T> objType) {
        try {
            T obj = ReflectUtils.newInstance(objType);
            for (int i = 0; i < properties.size(); i++) {
                Object value = tuple.get(i);
                String name = properties.get(i);
                org.apache.commons.beanutils.BeanUtils.setProperty(obj, name, value);
            }
            return obj;
        } catch (Exception e) {
            throw new StatusAndMessageError(-99, e);
        }
    }

    static public From<?, ?> joinFrom(From<?, ?> root, String propertyName, Map<String, Join<?, ?>> joinContext, JoinType joinType) {

        if (propertyName.indexOf(".") < 0) {
            Join<?, ?> join = joinContext.get(propertyName);
            if (join == null) {
                Path<?> path = root.get(propertyName);
                if (Map.class.isAssignableFrom(path.getJavaType())) {
                    join = root.joinMap(propertyName);
                } else if (Collection.class.isAssignableFrom(path.getJavaType())) {
                    join = root.joinList(propertyName);
                } else {
                    join = root.join(propertyName);
                }
                joinContext.put(propertyName, join);
            }
            return join;
        }
        //if propertyName = a.b.c then joinName = a.b
        String joinName = joinName(propertyName);

        if (joinContext.containsKey(joinName)) {
            return joinContext.get(joinName);
        } else {
            //String joinName = propertyName.substring(0,lastIndex);
            String[] properties = propertyName.split("\\.");
            joinName = properties[0];

            for (int i = 0; i < (properties.length - 1); i++) {
                if (i > 0) {
                    joinName = String.format("%s.%s", joinName, properties[i]);
                }
                if (joinContext.containsKey(joinName)) {
                    root = joinContext.get(joinName);
                } else {
                    Join<?, ?> join = root.join(properties[i], joinType);
                    joinContext.put(joinName, join);
                    root = join;
                }
            }
            return root;
        }
    }

    static public Predicate predicateForProperty(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, QueryConditionModel<?> qcm, Map<String, Join<?, ?>> joinContext) {
        try {
            String propertyName = qcm.path;
            JoinType joinType = qcm.getJoinType();

            //判断是否需要join
            boolean needJoin = qcm.isJoin();

            if (propertyName.indexOf(".") > 0)
                needJoin = true;

            From<?, ?> from;
            if (needJoin)
                from = joinFrom(root, propertyName, joinContext, joinType);
            else
                from = root;

            Class<? extends PredicateCreator> creatorType = qcm.getCreator();
            PredicateCreator creator = creatorType.newInstance();
            creator.configure(from, query, builder);
            return creator.predicateForCondition(qcm);
        } catch (Throwable e) {
            throw new StatusAndMessageError(-99, e);
        }
    }

//    static public Predicate predicateForProperty(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, Object model
//            , PropertyDescriptor propertyDescriptor, Map<String, Join<?, ?>> joinContext) {
//
//        Method method = propertyDescriptor.getReadMethod();
//        String propertyName = propertyDescriptor.getName();
//
//        //// FIXME: 16/1/22 ingore class property for every object
//        if ("class".equals(propertyName))
//            return null;
//
//        Object value = null;
//        try {
//            value = method.invoke(model);
//        } catch (Exception e) {
//            throw new StatusAndMessageError(-99, e);
//        }
//
//        QueryConditionModel qcm = new QueryConditionModel(propertyName, value);
//
//        try {
//            //没有注解
//            if (value != null && !method.isAnnotationPresent(QueryCondition.class)) {
//                Path<Object> path = root.get(propertyName);
//                if (Collection.class.isAssignableFrom(value.getClass())) {
//                    Collection<Object> c = (Collection<Object>) value;
//                    return JPA.collectionLogic(JPA.Operator.In, path, c, builder);
//                } else {
//                    return JPA.simpleLogic(JPA.Operator.Equal, path, value, builder);
//                }
//            } else {
//
//                QueryCondition queryCondition = method.getAnnotation(QueryCondition.class);
//
//                qcm.initWithAnnotation(queryCondition);
//            }
//
//            return predicateForProperty(root, query, builder, qcm, joinContext);
//        } catch (Throwable e) {
//            throw new StatusAndMessageError(-99, e);
//        }
//    }


    static public <R, E> Predicate queryForModel(Root<E> root, CriteriaQuery<R> query, CriteriaBuilder builder, List<QueryConditionModel<?>> queryModels, boolean distinct, Map<String, Join<?, ?>> joinContext) {
        List<Predicate> predicates = new LinkedList<>();

        predicates.add(JPA.oneEqualOneLogic(builder));

        Map<String, Join<?, ?>> joinMap = joinContext;

        if (joinMap == null) {
            joinMap = new HashMap<>();
        }

        for (QueryConditionModel<?> queryModel : queryModels) {

            Predicate p = predicateForProperty(root, query, builder, queryModel, joinMap);
            if (p != null)
                predicates.add(p);
        }

        if (distinct) {
            query.distinct(true);
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    static public List<QueryConditionModel<?>> createQueryModelsFromQueryObject(Object queryObject) {
        List<QueryConditionModel<?>> result = new LinkedList<>();

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(queryObject.getClass());

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            Method method = propertyDescriptor.getReadMethod();
            String propertyName = propertyDescriptor.getName();

            //// FIXME: 16/1/22 ingore class property for every object
            if ("class".equals(propertyName))
                return null;

            Object value = null;
            try {
                value = method.invoke(queryObject);
            } catch (Exception e) {
                throw new StatusAndMessageError(-99, e);
            }

            QueryConditionModel qcm = new QueryConditionModel(propertyName, value);


            //没有注解
            if (value != null && !method.isAnnotationPresent(QueryCondition.class)) {

                //qcm.setPath(propertyName);
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    //Collection<Object> c = (Collection<Object>) value;
                    qcm.setOperator(JPA.Operator.In);
                    //return JPA.collectionLogic(JPA.Operator.In, path, c, builder);
                } else {
                    qcm.setOperator(JPA.Operator.Equal);
                    //return JPA.simpleLogic(JPA.Operator.Equal, path, value, builder);
                }
            } else {

                QueryCondition queryCondition = method.getAnnotation(QueryCondition.class);

                qcm.initWithAnnotation(queryCondition);
            }


            result.add(qcm);
        }

        return result;
    }

//    static public <R, E> Predicate queryForModel(Root<E> root, CriteriaQuery<R> query, CriteriaBuilder builder, Object model, Map<String, Join<?, ?>> joinContext) {
//
////        List<Predicate> predicates = new LinkedList<>();
////
////        predicates.add(JPA.oneEqualOneLogic(builder));
////
////        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(model.getClass());
////
////        Map<String, Join<?, ?>> joinMap = joinContext;
////
////        if (joinMap == null) {
////            joinMap = new HashMap<>();
////        }
////
////        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
////
////            Predicate p = predicateForProperty(root, query, builder, model, propertyDescriptor, joinMap);
////            if (p != null)
////                predicates.add(p);
////        }
//
//
//        boolean distinct = false;
//        if (model.getClass().isAnnotationPresent(QueryRequest.class)) {
//            QueryRequest queryRequest = model.getClass().getAnnotation(QueryRequest.class);
//            if (queryRequest.distinct()) {
//                //query.distinct(true);
//                distinct = true;
//            }
//        }
//
//        List<QueryConditionModel<?>> queryModels = createQueryModelsFromQueryObject(model);
//
//        return queryForModel(root,query,builder,queryModels,distinct,joinContext);
//
//        //return builder.and(predicates.toArray(new Predicate[0]));
//
//    }

    static public void selectForModels(Root<?> root, CriteriaQuery<Tuple> query, CriteriaBuilder builder, List<ResultColumnModel> models, Map<String, Join<?, ?>> joinContext) {
        List<Selection<?>> selections = new LinkedList<>();
        if (joinContext == null) {
            joinContext = new HashMap<>();
        }

        for (ResultColumnModel resultColumnModel : models) {

            String queryPropertyName = resultColumnModel.getPath();

            //Method method = propertyDescriptor.getReadMethod();

            boolean needJoin = resultColumnModel.isJoin();
            JoinType joinType = resultColumnModel.getJoinType();
            From<?, ?> from = needJoin ? joinFrom(root, queryPropertyName, joinContext, joinType) : root;

            Selection<?> path = from.get(propertyName(queryPropertyName));

            selections.add(path);

        }

        if (selections.size() > 0) {
            query.multiselect(selections);
        }

    }

    static public List<ResultColumnModel> resultColumnModelsFromResultType(Class<?> responseType, List<String> objectPropertyNames) {
        List<ResultColumnModel> models = new LinkedList<>();

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(responseType);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            String objectPropertyName = propertyDescriptor.getName();
            if ("class".equals(objectPropertyName))
                continue;

            String queryPropertyName = objectPropertyName;

            Method method = propertyDescriptor.getReadMethod();

            if (method.isAnnotationPresent(ResultColumn.class)) {
                ResultColumn resultColumn = method.getAnnotation(ResultColumn.class);

                models.add(new ResultColumnModel(resultColumn));
            } else {
                models.add(new ResultColumnModel(queryPropertyName));
            }

            objectPropertyNames.add(objectPropertyName);
        }

        return models;
    }

    static public List<String> selectForClass(Root<?> root, CriteriaQuery<Tuple> query, CriteriaBuilder builder, Class<?> responseType, Map<String, Join<?, ?>> joinContext) {

        List<String> result = new LinkedList<>();

        List<ResultColumnModel> models = resultColumnModelsFromResultType(responseType, result);

        selectForModels(root, query, builder, models, joinContext);

        return result;
    }

    static public <E> Specification<E> selectSpecificationForModel(Specification<E> specification, List<ResultColumnModel> resultColumnModels) {
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = null;
                if (specification!=null)
                    predicate = specification.toPredicate(root, query, cb);
                else
                    predicate = cb.equal(cb.literal(1),1);

                selectForModels(root, (CriteriaQuery<Tuple>) query, cb, resultColumnModels, null);

                return predicate;
            }
        };
    }


    static public <E> Specification<E> querySpecificationForModel(final List<QueryConditionModel<?>> queryConditionModels, boolean distinct) {
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return Query.queryForModel(root, query, cb, queryConditionModels, distinct, null);
            }
        };
    }

    static public <E> Specification<E> querySpecificationForTwoModel(final List<QueryConditionModel<?>> queryConditionModels,final List<ResultColumnModel> resultColumnModels ) {
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Map<String, Join<?, ?>> joinContext = new HashMap<>();
                Predicate predicate =  Query.queryForModel(root, query, cb, queryConditionModels, false, joinContext);
                selectForModels(root, (CriteriaQuery<Tuple>) query, cb, resultColumnModels, joinContext);
                return predicate;
            }
        };
    }


    static public <E> Specification<E> specification(){
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return cb.equal(cb.literal(1),1);
            }
        };
    }


}
