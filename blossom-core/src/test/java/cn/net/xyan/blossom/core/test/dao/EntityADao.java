package cn.net.xyan.blossom.core.test.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.core.test.entity.TestEntityA;

/**
 * Created by zarra on 16/4/23.
 */
public interface EntityADao extends EasyJpaRepository<TestEntityA,String> {
}
