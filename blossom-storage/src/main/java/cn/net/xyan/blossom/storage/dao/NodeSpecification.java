package cn.net.xyan.blossom.storage.dao;

import cn.net.xyan.blossom.storage.entity.Node;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Created by zarra on 2016/10/19.
 */
public class NodeSpecification {
    public static Specification<Node> specificationByParent(Node parent){
        return new Specification<Node>() {
            @Override
            public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Node> parentPath = root.get("parent");
                if (parent == null){
                    return criteriaBuilder.isNull(parentPath);
                }else {
                    return criteriaBuilder.equal(parentPath,parent);
                }
            }
        };
    }
}
