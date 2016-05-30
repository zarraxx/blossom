package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.security.Permission;

/**
 * Created by zarra on 16/5/30.
 */
public interface PermissionDao extends EasyJpaRepository<Permission,String> {
}
