package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;

/**
 * Created by zarra on 16/5/14.
 */
public interface StatusDao extends EasyJpaRepository<StatusAndType,String> {
}
