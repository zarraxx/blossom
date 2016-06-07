package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public interface UIModuleDao extends EasyJpaRepository<Module,String> {

    @Query("select mp from Module m join m.essentialPermission mp where m = ?1 and mp not in ?2")
    List<Permission> permissionInModuleNotExistInCollection(Module module ,List<Permission> permissions);

    @Query("select count(mp) from Module m join m.essentialPermission mp where m = ?1 and mp not in ?2")
    int countPermissionInModuleNotExistInCollection(Module module ,List<Permission> permissions);

    @Query("select M from Module M left join M.catalogs C left join C.uiPages P " +
            "where 1=1 and P = :PAGE " +
            " and M not in ( " +
            "select  distinct m from UIPage p  join p.catalogs c join c.modules m join m.essentialPermission mp " +
            "where 1=1 " +
            "and p = :PAGE " +
            "and mp not in ( :PS ) )"
            )
    List<Module> queryPermitModuleInPageForUser(@Param("PAGE")UIPage uiPage, @Param("PS")List<Permission> permissions);

    @Query("select M from Module M left join M.catalogs  C where " +
            "1=1 " +
            "and C = :CATALOG " +
            "and M not in( " +
            "select  distinct m from Catalog c join c.modules m  join m.essentialPermission mp " +
            "where 1=1 " +
            "and c = :CATALOG " +
            "and mp not in ( :PS ) ) "
    )
    List<Module> queryPermitModuleInCatalogForUser(@Param("CATALOG")Catalog catalog, @Param("PS")List<Permission> permissions);
}
