package cn.net.xyan.blossom.declarative.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.declarative.entity.DynamicBeanDefinition;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zarra on 16/8/20.
 */
public interface BeanDefinitionDao extends EasyJpaRepository<DynamicBeanDefinition,String> {

    @Query("select d from DynamicBeanDefinition d where d.abandon <> true")
    List<DynamicBeanDefinition> queryAllAvailable();
}
