package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zarra on 16/5/30.
 */
public interface CatalogDao extends EasyJpaRepository<Catalog,String> {
    @Query("select cp from Catalog c join c.essentialPermission cp where c = ?1 and cp not in ?2")
    List<Permission> permissionInCatalogNotExistInCollection(Catalog catalog , List<Permission> permissions);

    @Query("select count(cp) from Catalog c join c.essentialPermission cp where c = ?1 and cp not in ?2")
    Integer countPermissionInCatalogNotExistInCollection(Catalog catalog , List<Permission> permissions);

    @Query("select C from Catalog C join C.uiPages P where C not in ( " +
            "select distinct c  from UIPage p join p.catalogs c join c.essentialPermission cp " +
            "where cp not in (:PS)\n" +
            "and p = :PAGE " +
            ")" +
            "and P = :PAGE")
    List<Catalog> queryPermitCatalogInPageForUser(@Param("PAGE") UIPage uiPage, @Param("PS") List<Permission> permissions);
}
