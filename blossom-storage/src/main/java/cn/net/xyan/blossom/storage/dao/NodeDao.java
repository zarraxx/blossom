package cn.net.xyan.blossom.storage.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.storage.entity.Node;

/**
 * Created by zarra on 2016/10/18.
 */
public interface NodeDao extends EasyJpaRepository<Node,String> {
    Node findByParentAndTitle(Node parent,String title);

}
