package cn.net.xyan.blossom.core.jpa.support;

import cn.net.xyan.blossom.core.jpa.utils.query.QueryConditionModel;
import cn.net.xyan.blossom.core.jpa.utils.query.ResultColumnModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Tuple;
import java.util.List;

/**
 * Created by zarra on 16/1/22.
 */
public interface IModelQuery<T> {
    //base api
    List<T> findAllForModel(List<QueryConditionModel<?>> queryConditionModels);
    Page<T> findAllForModel(List<QueryConditionModel<?>> queryConditionModels, Pageable pageable);

    List<Tuple> findAllForModel(Specification<T> specification,List<ResultColumnModel> paths);
    Page<Tuple> findAllForModel(Specification<T> specification, List<ResultColumnModel> paths,Pageable pageable);

    List<Tuple> findAllForModel(List<QueryConditionModel<?>> queryConditionModels,List<ResultColumnModel> paths);
    Page<Tuple> findAllForModel(List<QueryConditionModel<?>> queryConditionModels, List<ResultColumnModel> paths,Pageable pageable);

    //adv api
    List<T> findAllForModel(Object queryModel);
    Page<T> findAllForModel(Object queryModel, Pageable pageable);

    <R> List<R> findAllForModel(Object queryModel, Class<R> responseType);
    <R> Page<R> findAllForModel(Object queryModel, Class<R> responseType, Pageable pageable);

    <R> List<R> findAllForModel(Class<R> responseType);
    <R> Page<R> findAllForModel( Class<R> responseType, Pageable pageable);

}
