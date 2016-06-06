package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.platform.dao.GroupDao;
import cn.net.xyan.blossom.platform.dao.PermissionDao;
import cn.net.xyan.blossom.platform.dao.UserDao;
import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.service.DictService;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

/**
 * Created by zarra on 16/6/6.
 */
public class SecurityServiceImpl extends InstallerAdaptor implements SecurityService {

    @Autowired
    DictService dictService;

    @Autowired
    I18NService i18NService;

    @Autowired
    UserDao userDao;

    @Autowired
    GroupDao groupDao;

    @Autowired
    PermissionDao permissionDao;

    @Override
    public void beforeSetup() {
        dictService.setupStatus(UserStatus.class,ActiveStatus,"Active");
        dictService.setupStatus(UserStatus.class,InActiveStatus,"Abandon",true);
    }

    @Override
    public Permission setupPermission(String permissionCode, String title) {
        Permission permission = permissionDao.findOne(permissionCode);
        if (permission == null){
            permission = new Permission(permissionCode);
            String keyTitle = Permission.i18nTitleKey(permission);
            String keyDescribe = Permission.i18nDescribeKey(permission);

            I18NString iTitle = i18NService.setupMessage(keyTitle,title);
            I18NString iDescribe = i18NService.setupMessage(keyDescribe,"");

            permission.setTitle(iTitle);
            permission.setDescribe(iDescribe);
            permission = permissionDao.saveAndFlush(permission);
        }

        return permission;
    }

    @Override
    public Group setupGroup(String groupCode, String title) {
        Group group = groupDao.findOne(groupCode);
        if (group == null){
            group = new Group();
            group.setCode(groupCode);
            String keyTitle = Group.i18nTitleKey(group);
            String keyDescribe = Group.i18nDescribeKey(group);

            I18NString iTitle = i18NService.setupMessage(keyTitle,title);
            I18NString iDescribe = i18NService.setupMessage(keyDescribe,"");

            group.setTitle(iTitle);
            group.setDescribe(iDescribe);
            group = groupDao.saveAndFlush(group);
        }

        return group;
    }

    @Override
    public User setupUser(String loginName, String password, String realName, Collection<Permission> permissions) {
        User user = userDao.findOne(loginName);
        if (user == null){
            user = new User();
            user.setLoginName(loginName);
            user.setRealName(realName);
            user.setPassword(password);
            user.getPermissions().addAll(permissions);
            user = userDao.saveAndFlush(user);
        }
        return user;
    }

    @Override
    public Boolean isUserNameExist(String user) {
        User databaseUser = userDao.findOne(user);
        return databaseUser!=null;
    }

    @Override
    public User queryUserByUsername(String username) {
        return userDao.findOne(username);
    }

    @Override
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        return userDao.findOne(name);
    }

    @Override
    public Boolean checkPermissionForUser(User user, Permission... permissions) {
        return null;
    }

    @Override
    public Boolean checkPermissionForUser(User user, List<Permission> permissions) {
        List<Permission> permissionList = permissionDao.checkUserHasPermissions(user,permissions);
        return permissionList.size() == permissions.size();
    }

    @Override
    public List<Permission> queryPermissionForUser(User user) {
        return permissionDao.findAllPermissionForUser(user);
    }
}
