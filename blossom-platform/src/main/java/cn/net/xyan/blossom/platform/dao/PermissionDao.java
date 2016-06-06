package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public interface PermissionDao extends EasyJpaRepository<Permission,String> {

    @Query("select distinct  p from Permission p   " +
            "where p.code in (select p.code from Permission p left join p.users u1 where u1 = ?1 ) or " +
            "p.code in  (select p.code from Permission p inner join  p.groups g left join g.users u2 where u2 = ?1 and g.status.abandon = false )  ")
    List<Permission> findAllPermissionForUser(User user);

    @Query("select distinct  p from Permission p  right join  p.groups g where g in ?1 ")
    List<Permission> findAllPermissionForGroups(Collection<Group> groups);

    Permission findByCode(String code);

    @Query("select distinct  p from Permission p   " +
            "where "
            +"p.code in (select p.code from Permission p left join p.users u1 where u1 = ?1 and p = ?2 ) or " +
            "p.code in  (select p.code from Permission p inner join  p.groups g left join g.users u2 where u2 = ?1 and g.status.abandon = false and p = ?2)  ")
    Permission checkUserHasPermission(User user,Permission permission);

    @Query("select distinct  p from Permission p   " +
            "where "
            +"p.code in (select p.code from Permission p left join p.users u1 where u1 = ?1 and p in ?2 ) or " +
            "p.code in  (select p.code from Permission p inner join  p.groups g left join g.users u2 where u2 = ?1 and g.status.abandon = false and p in ?2)  ")
    List<Permission> checkUserHasPermissions(User user,Collection<Permission> permissions);

    @Query("select p from Permission p where p not in ?1")
    List<Permission> findPermissionNotIn(Collection<Permission> permissions);
}
