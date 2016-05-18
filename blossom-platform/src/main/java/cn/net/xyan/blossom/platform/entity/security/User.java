package cn.net.xyan.blossom.platform.entity.security;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_security_user")
public class User {

    String loginName;
    String realName;
    String password;
    Date   createDate;
    Date   lastLogin;

    List<Permission> permissions;
    List<Group> groups;

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Id
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @ManyToMany
    @JoinTable(name = "sys_user_group",
            joinColumns = {
                    @JoinColumn(name = "c_user"),
            },
            inverseJoinColumns = @JoinColumn(name = "c_group")
    )
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @ManyToMany
    @JoinTable(name = "sys_user_permission",
            joinColumns = {
                    @JoinColumn(name = "c_user"),
            },
            inverseJoinColumns = @JoinColumn(name = "c_permission")
    )
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
