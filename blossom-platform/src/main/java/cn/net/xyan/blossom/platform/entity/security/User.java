package cn.net.xyan.blossom.platform.entity.security;

import cn.net.xyan.blossom.platform.entity.ComparableEntity;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_security_user")
public class User extends ComparableEntity<User> {

    String loginName;
    String realName;
    String password;
    Date   createDate;
    Date   lastLogin;

    SortedSet<Permission> permissions;
    SortedSet<Group> groups;

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
    @SortNatural
    public SortedSet<Group> getGroups() {
        return groups;
    }

    public void setGroups(SortedSet<Group> groups) {
        this.groups = groups;
    }

    @ManyToMany
    @JoinTable(name = "sys_user_permission",
            joinColumns = {
                    @JoinColumn(name = "c_user"),
            },
            inverseJoinColumns = @JoinColumn(name = "c_permission")
    )
    @SortNatural
    public SortedSet<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(SortedSet<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int compareTo(User o) {
         int value = super.compareTo(o);
        if (value ==0 ){
            if (getCreateDate() != null)
                value = getCreateDate().compareTo(o.getCreateDate());

        }

        if (value == 0)
            value = getLoginName().compareTo(o.getLoginName());

        return value;

    }
}
