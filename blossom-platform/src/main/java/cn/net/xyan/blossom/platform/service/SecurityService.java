package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

/**
 * Created by zarra on 16/6/6.
 */
public interface SecurityService extends Installer,UserDetailsService {

    String USERAdmin       = "admin";
    String GroupAdmin      = "admin";
    String PermissionAdmin = "admin";

    Integer ActiveStatus = 1;
    Integer InActiveStatus = -1;

    //Core api
    Permission setupPermission(String permissionCode,String title);

    Group setupGroup(String groupCode,String title,Permission ... permissions);

    User setupUser(String loginName, String password, String realName, Group ... groups);

    Boolean isUserNameExist(String user);

    Boolean isUserBlocked(String user);

    User queryUserByUsername(String username);

    User currentUser();

    Boolean checkPermissionForUser(User user,Permission ...permissions);
    Boolean checkPermissionForUser(User user,List<Permission> permissions);

    List<Permission> queryPermissionForUser(User user);

    //For UI api

    Boolean checkCatalogPermitForUser(Catalog catalog,User user);
    Boolean checkModulePermitForUser(Module module, User user);

    List<Catalog> catalogsPermitInPageForUser(UIPage page,User user);

    List<Module> modulePermitInPageForUser(UIPage page,User user);
    List<Module> modulePermitInCatalogForUser(Catalog catalog,User user);
}
