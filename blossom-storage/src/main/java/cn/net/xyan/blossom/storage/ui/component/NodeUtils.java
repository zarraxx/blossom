package cn.net.xyan.blossom.storage.ui.component;

import cn.net.xyan.blossom.storage.entity.Node;
import com.vaadin.addon.jpacontainer.JPAContainer;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by zarra on 2017/2/6.
 */
public class NodeUtils {

    public static List<Node> query(Specification<Node> specification, JPAContainer<Node> jpaContainer){
        EntityManager em  = jpaContainer.getEntityProvider().getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Node> query = builder.createQuery(Node.class);
        Root<Node> root = query.from(Node.class);

        Predicate predicate = specification.toPredicate(root,query,builder);

        query.where(predicate);

        return em.createQuery(query).getResultList();
    }
}
