package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;

import java.util.Collection;
import java.util.List;

/**
 * Created by zarra on 16/6/6.
 */
public interface SecurityService extends Installer {

    String USERAdmin       = "admin";
    String GroupAdmin      = "admin";
    String PermissionAdmin = "admin";

    Integer ActiveStatus = 1;
    Integer InActiveStatus = 2;

    Permission setupPermission(String permissionCode,String title);

    Group setupGroup(String groupCode,String title);

    User setupUser(String loginName, String password, String realName, Collection<Permission> permissions);

    Boolean isUserNameExist(String user);
    User queryUserByUsername(String username);

    User currentUser();

    Boolean checkPermissionForUser(User user,Permission ...permissions);
    Boolean checkPermissionForUser(User user,List<Permission> permissions);

    List<Permission> queryPermissionForUser(User user);

}
