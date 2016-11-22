package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.platform.dao.*;
import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.UIPage;
import cn.net.xyan.blossom.platform.entity.dict.GroupStatus;
import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.model.SecurityUserDetails;
import cn.net.xyan.blossom.platform.service.DictService;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.service.SecurityService;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

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

    @Autowired
    UIModuleDao moduleDao;

    @Autowired
    CatalogDao catalogDao;

    @Override
    public void beforeSetup() {
        UserStatus usActive = dictService.setupStatus(UserStatus.class,ActiveStatus,"Active");
        dictService.setupStatus(UserStatus.class,InActiveStatus,"Abandon",true);

        GroupStatus gActive = dictService.setupStatus(GroupStatus.class,ActiveStatus,"Active");
        dictService.setupStatus(GroupStatus.class,InActiveStatus,"Abandon",true);

        Permission superPermission = setupPermission(PermissionAdmin,PermissionAdmin);

        Group superGroup = setupGroup(GroupAdmin,PermissionAdmin,superPermission);

        User superUser = setupUser(USERAdmin,USERAdmin,USERAdmin,superGroup);
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
    public Group setupGroup(String groupCode, String title,Permission ... permissions) {
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
            group.getPermissions().addAll(Arrays.asList(permissions) );
            group.setStatus(dictService.findStatus(GroupStatus.class, ActiveStatus));
            group = groupDao.saveAndFlush(group);
        }

        return group;
    }

    @Override
    public User setupUser(String loginName, String password, String realName, Group ... groups) {
        User user = queryUserByUsername(loginName);
        if (user == null){
            user = new User();
            user.setLoginName(loginName);
            user.setRealName(realName);
            user.setPassword(password);
            user.getGroups().addAll(Arrays.asList(groups));
            user.setCreateDate(new Date());
            user.setStatus(dictService.findStatus(UserStatus.class, ActiveStatus));
            user = userDao.saveAndFlush(user);
        }
        return user;
    }

    @Override
    public Group findGroup(String groupCode) {
        return groupDao.findOne(groupCode);
    }

    @Override
    public User findUser(String username) {
        return userDao.findOne(username);
    }

    @Override
    public Permission findPermission(String permissionCode) {
        return permissionDao.findByCode(permissionCode);
    }

    @Override
    public Boolean isUserNameExist(String user) {
        User databaseUser = queryUserByUsername(user);
        return databaseUser!=null;
    }

    @Override
    public Boolean isUserBlocked(String user) {
        int v =  userDao.isUserBlocked(user);
        return v >0 ;
    }

    @Override
    public User queryUserByUsername(String username) {
        return userDao.findOne(username);
    }

    @Override
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        String name = auth.getName(); //get logged in username
        return userDao.findOne(name);
    }

    @Override
    public Boolean checkPermissionForUser(User user, Permission... permissions) {
        if(user == null) return false;
        return checkPermissionForUser(user, Arrays.asList(permissions));
    }

    @Override
    public Boolean checkPermissionForUser(User user, List<Permission> permissions) {
        if(user == null) return false;
        Permission superPermission = permissionDao.findByCode(PermissionAdmin);
        if ( superPermission !=null && permissionDao.checkUserHasPermission(user,superPermission)!=null){
            return true;
        }
        List<Permission> permissionList = permissionDao.checkUserHasPermissions(user,permissions);
        return permissionList.size() == permissions.size();
    }

    @Override
    public List<Permission> queryPermissionForUser(User user) {
        if(user == null) return new LinkedList<>();
        Permission superPermission = permissionDao.findByCode(PermissionAdmin);
        if (superPermission !=null && permissionDao.checkUserHasPermission(user,superPermission)!= null){
            return permissionDao.findAll();
        }else{
            return permissionDao.findAllPermissionForUser(user);
        }
    }



    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        UserDetails userDetails = null;
        User user = null;
        if (s!=null)
            user = queryUserByUsername(s);
        Collection<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        if (user == null || user.getStatus() == null ){
            user = new User();
            user.setLoginName(s);
            user.setPassword("");
            grantedAuths = AuthorityUtils.NO_AUTHORITIES;
            userDetails = new SecurityUserDetails(user,
                    false, false, false, false,
                    grantedAuths);
        }
        else{

            boolean accountLocked =  isUserBlocked(user.getLoginName());

            boolean accountNonLocked = !accountLocked;

            for(Permission permission : queryPermissionForUser(user)){
                grantedAuths.add(new SimpleGrantedAuthority(String.format("ROLE_%s",permission.getCode())));
            }
            userDetails = new SecurityUserDetails(user,
                    true, true, true, accountNonLocked,
                    grantedAuths);
        }

        return userDetails;

    }



    @Override
    public Boolean checkCatalogPermitForUser(Catalog catalog, User user) {
        if(user == null) return false;
        List<Permission> permissions = queryPermissionForUser(user);
        if (permissions.size() == 0) return catalog.getEssentialPermission().size() == 0;
        int n = catalogDao.countPermissionInCatalogNotExistInCollection(catalog,permissions);
        return n == 0;
    }

    @Override
    public Boolean checkModulePermitForUser(Module module, User user) {
        if(user == null) return false;
        List<Permission> permissions = queryPermissionForUser(user);
        if (permissions.size() == 0) return module.getEssentialPermission().size() == 0;
        int n = moduleDao.countPermissionInModuleNotExistInCollection(module,permissions);
        return n == 0;
    }

    @Override
    public List<Catalog> catalogsPermitInPageForUser(UIPage page, User user) {
        List<Permission> permissions = queryPermissionForUser(user);
        if (permissions.size() == 0) return catalogDao.publicCatalogInPage(page);
        return catalogDao.queryPermitCatalogInPageForUser(page,permissions);
    }

    @Override
    public List<Module> modulePermitInPageForUser(UIPage page, User user) {
        List<Permission> permissions = queryPermissionForUser(user);
        if (permissions.size() == 0) return moduleDao.publicModuleInPage(page);
        return moduleDao.queryPermitModuleInPageForUser(page,permissions);
    }

    @Override
    public List<Module> modulePermitInCatalogForUser(Catalog catalog, User user) {
        List<Permission> permissions = queryPermissionForUser(user);
        if (permissions.size() == 0) return  moduleDao.publicModuleInCatalog(catalog);
        return moduleDao.queryPermitModuleInCatalogForUser(catalog,permissions);
    }
}
