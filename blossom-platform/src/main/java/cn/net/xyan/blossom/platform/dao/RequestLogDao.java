package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.log.RequestLog;

/**
 * Created by zarra on 16/8/19.
 */
public interface RequestLogDao extends EasyJpaRepository<RequestLog,String> {
}
