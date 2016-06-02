package cn.net.xyan.blossom.core.support;

import com.vaadin.addon.jpacontainer.EntityManagerProvider;

import javax.persistence.EntityManager;

/**
 * Created by zarra on 16/6/2.
 */
public class SpringEntityManagerProviderFactory {


    EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public SpringEntityManagerProvider create(){
        return new SpringEntityManagerProvider();
    }

    public  class SpringEntityManagerProvider implements EntityManagerProvider {

        @Override
        public EntityManager getEntityManager() {
            return entityManager;
        }
    }
}
