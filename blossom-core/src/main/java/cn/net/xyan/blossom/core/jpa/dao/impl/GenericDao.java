package cn.net.xyan.blossom.core.jpa.dao.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

/**
 * Created by zarra on 16/3/8.
 */
public class GenericDao {

    @PersistenceContext
    EntityManager em;


    public String getEntityName(Class<?> cls) {
        String entity;

        Entity entityAnn = cls.getAnnotation(Entity.class);

        if (entityAnn != null && !entityAnn.name().equals("")) {
            entity = entityAnn.name();
        } else {
            entity = cls.getSimpleName();
        }

        return entity;

    }


    protected <T> TypedQuery<Long> getCountQuery(Class<T> tClass) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = query.from(tClass);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return em.createQuery(query);
    }

    protected  Long executeCountQuery2(TypedQuery<Long> query) {

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    <T> CriteriaQuery<T> findQuery(Class<T> tClass){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(tClass);
        Root<T> root = query.from(tClass);

        return query;
    }

    public <T> T save(T entity){
        if (em.contains(entity)){
            return em.merge(entity);
        }else{
            em.persist(entity);
            return entity;
        }
    }

    public <T,PK> T findOne(Class<T> tClass,PK pk){
        return em.find(tClass,pk);
    }

    public <T> Page<T> findAll(Class<T> tClass, Pageable pageable) {

        CriteriaQuery<T> query = findQuery(tClass);
        TypedQuery<T> typedQuery = em.createQuery(query);

        if (pageable == null)
            return new PageImpl<>(typedQuery.getResultList());
        else {
            typedQuery.setFirstResult(pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            Long total = executeCountQuery2(getCountQuery(tClass));

            List<T> content = total > pageable.getOffset() ? typedQuery.getResultList() : Collections.<T>emptyList();

            return new PageImpl<>(content, pageable, total);
        }
    }
}
