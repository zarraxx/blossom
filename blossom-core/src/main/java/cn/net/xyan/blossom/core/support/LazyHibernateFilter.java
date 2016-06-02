package cn.net.xyan.blossom.core.support;

import com.vaadin.addon.jpacontainer.EntityManagerProvider;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.servlet.*;
import java.io.IOException;

/**
 * Created by zarra on 16/5/13.
 */
public class LazyHibernateFilter implements Filter {

    public static class LazyHibernateEntityManagerProvider implements EntityManagerProvider {

        static ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

        @Override
        public EntityManager getEntityManager() {
            EntityManager em = entityManagerThreadLocal.get();
            return em;
        }

        public void close() {
            EntityManager em = entityManagerThreadLocal.get();
            if (em != null && em.isOpen()) {
                em.close();
                entityManagerThreadLocal.set(null);
            }
        }

        public static void setCurrentEntityManager(EntityManager em) {
            entityManagerThreadLocal.set(em);
        }

    }

    Logger logger = LoggerFactory.getLogger(LazyHibernateFilter.class);


    EntityManagerFactory emf;
    //JpaTransactionManager transactionManager;

    @PersistenceUnit
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
       // transactionManager = new JpaTransactionManager(emf);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

//            try {
//                TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
//            }catch (Throwable e){
//                throw  e;
//            }
//
//            DefaultTransactionDefinition def = new DefaultTransactionDefinition();//事务定义类
//            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//            TransactionStatus status = transactionManager.getTransaction(def);
//
            LazyHibernateEntityManagerProvider.setCurrentEntityManager(em);
           // Session session = em.unwrap(Session.class);

            try {
               // session.beginTransaction();
                filterChain.doFilter(servletRequest, servletResponse);
               // session.getTransaction().commit();
            }catch (Throwable e){
                //et.rollback();
               // session.getTransaction().rollback();
                //transactionManager.rollback(status);
                throw  e;
            }finally{
               //session.close();
//                EntityManagerHolder emHolder = (EntityManagerHolder)
//                        TransactionSynchronizationManager.unbindResource(emf);
//                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
            }

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            LazyHibernateEntityManagerProvider.setCurrentEntityManager(null);
        }
    }

    @Override
    public void destroy() {

    }
}
