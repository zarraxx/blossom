package cn.net.xyan.blossom.core.support;

import com.vaadin.addon.jpacontainer.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.*;
import java.io.IOException;

/**
 * Created by zarra on 16/5/13.
 */
public class LazyHibernateFilter implements Filter {

    public static class LazyHibernateEntityManagerProvider implements EntityManagerProvider {

        static ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

        public static LazyHibernateFilter lazyHibernateFilter;

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

    @PersistenceUnit
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            LazyHibernateEntityManagerProvider.setCurrentEntityManager(em);

            filterChain.doFilter(servletRequest, servletResponse);

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
