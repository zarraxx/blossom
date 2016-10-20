package cn.net.xyan.blossom.storage.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.storage.entity.BinaryDataHolder;

/**
 * Created by zarra on 2016/10/18.
 */
public interface BinaryDataDao extends EasyJpaRepository<BinaryDataHolder,String> {
}
