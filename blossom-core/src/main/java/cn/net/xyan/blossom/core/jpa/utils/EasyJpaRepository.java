package cn.net.xyan.blossom.core.jpa.utils;

import cn.net.xyan.blossom.core.jpa.support.IModelQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

/**
 * Created by xiashenpin on 16/1/16.
 */
public interface EasyJpaRepository<T,PK extends Serializable>  extends JpaRepository<T,PK>,JpaSpecificationExecutor<T>,IModelQuery<T> {
}
