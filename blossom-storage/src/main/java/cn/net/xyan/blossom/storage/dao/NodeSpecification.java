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
            public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<Node> parentPath = root.get("parent");
                if (parent == null){
                    return cb.isNull(parentPath);
                }else {
                    return cb.equal(parentPath,parent);
                }
            }
        };
    }

    public static Specification<Node> specificationByType(Class<? extends Node> nClass){
        return new Specification<Node>() {
            @Override
            public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.type(), cb.literal(nClass));
            }
        };
    }

    public static Specification<Node> specificationByTypeNot(Class<? extends Node> nClass){
        return new Specification<Node>() {
            @Override
            public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.notEqual(root.type(), cb.literal(nClass));
            }
        };
    }
}
