package cn.net.xyan.blossom.platform.service;

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
    Integer InActiveStatus = 2;

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

}
