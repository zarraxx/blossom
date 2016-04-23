package cn.net.xyan.blossom.core.jpa.support;


import cn.net.xyan.blossom.core.jpa.utils.query.Query;
import cn.net.xyan.blossom.core.jpa.utils.query.QueryConditionModel;
import cn.net.xyan.blossom.core.jpa.utils.query.ResultColumnModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;


/**
 * Created by zarra on 16/1/22.
 */
@NoRepositoryBean
public class EasyJpaRepositoryImpl<ENTITY, ID extends Serializable>
        extends SimpleJpaRepository<ENTITY, ID> implements IModelQuery<ENTITY> {

    private EntityManager em;

    public EasyJpaRepositoryImpl(JpaEntityInformation<ENTITY, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }

    public EasyJpaRepositoryImpl(Class<?> domainClass, EntityManager em) {
        super((Class<ENTITY>) domainClass, em);
        this.em = em;
    }


    protected static Long executeCountQuery2(TypedQuery<Long> query) {

        Assert.notNull(query);

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    @Override
    public List<ENTITY> findAllForModel(List<QueryConditionModel<?>> queryConditionModels) {
        Specification<ENTITY> specification = Query.querySpecificationForModel(queryConditionModels, false);
        return findAll(specification);
    }

    @Override
    public Page<ENTITY> findAllForModel(List<QueryConditionModel<?>> queryConditionModels, Pageable pageable) {
        Specification<ENTITY> specification = Query.querySpecificationForModel(queryConditionModels, false);
        return findAll(specification, pageable);
    }

    protected TypedQuery<Tuple> getTupleQuery(Specification<ENTITY> s) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<ENTITY> root = query.from(getDomainClass());

        Predicate predicate = s.toPredicate(root,query,builder);

        query.where(predicate);

        return em.createQuery(query);
    }



    @Override
    public List<Tuple> findAllForModel(Specification<ENTITY> specification, List<ResultColumnModel> paths) {

        Specification<ENTITY> s = Query.selectSpecificationForModel(specification, paths);
        TypedQuery<Tuple> q = getTupleQuery(s);
        return q.getResultList();
    }

    @Override
    public Page<Tuple> findAllForModel(Specification<ENTITY> specification, List<ResultColumnModel> paths, Pageable pageable) {
        Specification<ENTITY> s = Query.selectSpecificationForModel(specification, paths);
        return allTuple(s,pageable);
    }

    @Override
    public List<Tuple> findAllForModel(List<QueryConditionModel<?>> queryConditionModels, List<ResultColumnModel> paths) {
        Specification<ENTITY> s = Query.querySpecificationForTwoModel(queryConditionModels,paths);
        TypedQuery<Tuple> q = getTupleQuery(s);
        return q.getResultList();
    }

    @Override
    public Page<Tuple> findAllForModel(List<QueryConditionModel<?>> queryConditionModels, List<ResultColumnModel> paths, Pageable pageable) {
        Specification<ENTITY> s = Query.querySpecificationForTwoModel(queryConditionModels,paths);
        return allTuple(s,pageable);
    }

    @Override
    public List<ENTITY> findAllForModel(Object queryModel) {
        List<QueryConditionModel<?>> models = Query.createQueryModelsFromQueryObject(queryModel);
        return findAllForModel(models);
    }

    @Override
    public Page<ENTITY> findAllForModel(Object queryModel, Pageable pageable) {
        List<QueryConditionModel<?>> models = Query.createQueryModelsFromQueryObject(queryModel);
        return findAllForModel(models,pageable);
    }

    @Override
    public <R> List<R> findAllForModel(Object queryModel, Class<R> responseType) {
        List<String> objectPropertyNames = new LinkedList<>();
        List<QueryConditionModel<?>> models = Query.createQueryModelsFromQueryObject(queryModel);
        List<ResultColumnModel> resultColumnModels = Query.resultColumnModelsFromResultType(responseType,objectPropertyNames);
        Specification<ENTITY> s = Query.querySpecificationForTwoModel(models,resultColumnModels);
        Page<Tuple> tuples = allTuple(s,null);
        return convertTupleList2ObjList(tuples.getContent(),objectPropertyNames,responseType);
    }

    @Override
    public <R> Page<R> findAllForModel(Object queryModel, Class<R> responseType, Pageable pageable) {
        List<String> objectPropertyNames = new LinkedList<>();
        List<QueryConditionModel<?>> models = Query.createQueryModelsFromQueryObject(queryModel);
        List<ResultColumnModel> resultColumnModels = Query.resultColumnModelsFromResultType(responseType,objectPropertyNames);
        Specification<ENTITY> s = Query.querySpecificationForTwoModel(models,resultColumnModels);
        Page<Tuple> tuples = allTuple(s,pageable);

        return convertTuplePage2ObjPage(tuples,objectPropertyNames,responseType,pageable);
    }

    @Override
    public <R> List<R> findAllForModel(Class<R> responseType) {
        List<String> objectPropertyNames = new LinkedList<>();
        List<ResultColumnModel> resultColumnModels = Query.resultColumnModelsFromResultType(responseType,objectPropertyNames);
        List<Tuple> tuples = findAllForModel(Query.specification(),resultColumnModels);
        return convertTupleList2ObjList(tuples,objectPropertyNames,responseType);
    }

    @Override
    public <R> Page<R> findAllForModel(Class<R> responseType, Pageable pageable) {
        List<String> objectPropertyNames = new LinkedList<>();
        List<ResultColumnModel> resultColumnModels = Query.resultColumnModelsFromResultType(responseType,objectPropertyNames);
        Page<Tuple> tuples = findAllForModel(Query.specification(),resultColumnModels,pageable);
        return convertTuplePage2ObjPage(tuples,objectPropertyNames,responseType,pageable);
    }

//    @Override
//    public List<Tuple> findAllForModel(Specification<ENTITY> specification, List<String> paths) {
//        return null;
//    }
//
//    @Override
//    public Page<Tuple> findAllForModel(Specification<ENTITY> specification, List<String> paths, Pageable pageable) {
//        return null;
//    }
//
//    protected Page<Tuple> queryForModelAndResultType(Specifications<ENTITY> specification, Class<?> responseType, Pageable pageable, List<String> objProperties) {
//
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Tuple> query = builder.createTupleQuery();
//        Root<ENTITY> root = query.from(getDomainClass());
//
//        Map<String, Join<?, ?>> joinContext = new HashMap<>();
//
//        Predicate predicate = specification.toPredicate(root, query, builder);
//
//        List<String> properties = Query.selectForClass(root, query, builder, responseType, joinContext);
//
//        objProperties.addAll(properties);
//
//        query.where(predicate);
//
//        TypedQuery<Tuple> tupleTypedQuery = em.createQuery(query);
//
//        if (pageable == null)
//            return new PageImpl<>(tupleTypedQuery.getResultList());
//
//        tupleTypedQuery.setFirstResult(pageable.getOffset());
//        tupleTypedQuery.setMaxResults(pageable.getPageSize());
//
//        Long total = executeCountQuery2(getCountQuery(specification));
//
//        List<Tuple> content = total > pageable.getOffset() ? tupleTypedQuery.getResultList() : Collections.<Tuple>emptyList();
//
//        return new PageImpl<>(content, pageable, total);
//
//    }
//
//
//    protected CriteriaQuery<Tuple> queryForModelAndResultType(Object model, Class<?> responseType, List<String> objProperties) {
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Tuple> query = builder.createTupleQuery();
//        Root<ENTITY> root = query.from(getDomainClass());
//
//        Map<String, Join<?, ?>> joinContext = new HashMap<>();
//
//        Predicate predicate = Query.queryForModel(root, query, builder, model, joinContext);
//
//        List<String> properties = Query.selectForClass(root, query, builder, responseType, joinContext);
//
//        objProperties.addAll(properties);
//
//        query.where(predicate);
//
//        return query;
//    }
//
//
//    protected Page<Tuple> queryForModelAndResultType(Object model, Class<?> responseType, Pageable pageable, List<String> objProperties) {
//
//        CriteriaQuery<Tuple> query = queryForModelAndResultType(model, responseType, objProperties);
//
//        TypedQuery<Tuple> tupleTypedQuery = em.createQuery(query);
//
//        if (pageable == null)
//            return new PageImpl<>(tupleTypedQuery.getResultList());
//
//        tupleTypedQuery.setFirstResult(pageable.getOffset());
//        tupleTypedQuery.setMaxResults(pageable.getPageSize());
//
//        Long total = executeCountQuery2(getCountQuery(Query.<ENTITY>querySpecificationForModel(model)));
//
//        List<Tuple> content = total > pageable.getOffset() ? tupleTypedQuery.getResultList() : Collections.<Tuple>emptyList();
//
//        return new PageImpl<>(content, pageable, total);
//
//    }
//

//
//
//
//    @Override
//    public List<ENTITY> findAllForModel(final Object queryModel) {
//        List<QueryConditionModel<?>> queryConditionModels = Query.createQueryModelsFromQueryObject(queryModel);
//        return findAllForModel(queryConditionModels);
//    }
//
//    @Override
//    public Page<ENTITY> findAllForModel(Object queryModel, Pageable pageable) {
//        List<QueryConditionModel<?>> queryConditionModels = Query.createQueryModelsFromQueryObject(queryModel);
//        return findAllForModel(queryConditionModels,pageable);
//    }
//
//    @Override
//    public <R> List<R> findAllForModel(Object queryModel, Class<R> responseType) {
//        return findAllForModel(queryModel, responseType, null).getContent();
//    }
//
//    @Override
//    public <R> Page<R> findAllForModel(Object queryModel, Class<R> responseType, Pageable pageable) {
//        List<String> objProperties = new LinkedList<>();
//        Page<Tuple> tuplePage = queryForModelAndResultType(queryModel, responseType, pageable, objProperties);
//        return convertTuplePage2ObjPage(tuplePage, objProperties, responseType, pageable);
//    }
//
//    @Override
//    public <R> List<R> findAllForModel(Specification<ENTITY> specification, Class<R> responseType) {
//        return findAllForModel(specification, responseType, null).getContent();
//    }
//
//    @Override
//    public <R> Page<R> findAllForModel(Specification<ENTITY> specification, Class<R> responseType, Pageable pageable) {
//        List<String> objProperties = new LinkedList<>();
//        Page<Tuple> tuplePage = queryForModelAndResultType(specification, responseType, pageable, objProperties);
//        return convertTuplePage2ObjPage(tuplePage, objProperties, responseType, pageable);
//    }
//

    protected <T> List<T> convertTupleList2ObjList(List<Tuple> list, List<String> objProperties, Class<T> resultType) {
        List<T> result = new LinkedList<>();
        for (Tuple tuple:list){
            result.add(Query.tuple2Object(tuple,objProperties,resultType));
        }
        return result;
    }

    protected <T> Page<T> convertTuplePage2ObjPage(Page<Tuple> page, List<String> objProperties, Class<T> resultType, Pageable pageable) {
        List<T> content = new LinkedList<>();
        for (Tuple tuple : page.getContent()) {
            content.add(Query.tuple2Object(tuple, objProperties, resultType));
        }

        Long total = page.getTotalElements();
        return new PageImpl<>(content, pageable, total);
    }

    public Page<Tuple> allTuple(Specification<ENTITY> specification, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<ENTITY> root = query.from(getDomainClass());

        Predicate predicate = specification.toPredicate(root, query, builder);

        query.where(predicate);

        TypedQuery<Tuple> tupleTypedQuery = em.createQuery(query);

        if (pageable == null)
            return new PageImpl<>(tupleTypedQuery.getResultList());

        tupleTypedQuery.setFirstResult(pageable.getOffset());
        tupleTypedQuery.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery2(getCountQuery(specification));

        List<Tuple> content = total > pageable.getOffset() ? tupleTypedQuery.getResultList() : Collections.<Tuple>emptyList();

        return new PageImpl<>(content, pageable, total);
    }
//
//    @Override
//    public List<Tuple> findAllForTuple(Specification<ENTITY> specification) {
//        return findAllForTuple(specification,null).getContent();
//    }


}
