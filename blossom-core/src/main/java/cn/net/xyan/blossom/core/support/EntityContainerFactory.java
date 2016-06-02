package cn.net.xyan.blossom.core.support;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingLocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.LocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.util.HibernateLazyLoadingDelegate;

/**
 * Created by zarra on 16/5/13.
 */
public class EntityContainerFactory {
    static protected void initEntityProviderInnel(EntityProvider<?> entityProvider){

        LazyHibernateFilter.LazyHibernateEntityManagerProvider provider = new LazyHibernateFilter.LazyHibernateEntityManagerProvider();
        HibernateLazyLoadingDelegate hibernateLazyLoadingDelegate  = new HibernateLazyLoadingDelegate();
        entityProvider.setLazyLoadingDelegate(hibernateLazyLoadingDelegate);
        entityProvider.setEntityManagerProvider(provider);

    }

    static protected <T> JPAContainer<T> makeJPAContainer(Class<T> tClass,EntityProvider<T> entityProvider){
        JPAContainer<T> container = new JPAContainer<>(tClass);
        container.setEntityProvider(entityProvider);
        return container;
    }

    static public <T> JPAContainer<T> jpaContainer(Class<T> tClass){

        MutableLocalEntityProvider<T> entityProvider = new CachingMutableLocalEntityProvider<>(tClass);
        initEntityProviderInnel(entityProvider);
        return makeJPAContainer(tClass,entityProvider);

    }

    static public <T> JPAContainer<T> jpaContainerReadOnly(Class<T> tClass){

        //LocalEntityProvider<T> entityProvider = new CachingLocalEntityProvider(tClass);
        LocalEntityProvider<T> entityProvider = new LocalEntityProvider(tClass);
        initEntityProviderInnel(entityProvider);
        return makeJPAContainer(tClass,entityProvider);
    }
}
