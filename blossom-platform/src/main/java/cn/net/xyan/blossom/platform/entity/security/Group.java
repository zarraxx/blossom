package cn.net.xyan.blossom.platform.entity.security;

import cn.net.xyan.blossom.platform.entity.dict.GroupStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_group")
public class Group {
    String code;
    I18NString title;
    I18NString describe;

    GroupStatus status;

    Set<Permission> permissions = new HashSet<>();
    Set<User> users  = new HashSet<>();

    public static String i18nTitleKey(Group group){
        return String.format("group.title.%s",group.getCode());
    }

    public static String i18nDescribeKey(Group group){
        return String.format("group.describe.%s",group.getCode());
    }

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "c_describe")
    public I18NString getDescribe() {
        return describe;
    }

    public void setDescribe(I18NString describe) {
        this.describe = describe;
    }

    @ManyToMany
    @JoinTable(name = "sys_group_permission",
            joinColumns = {
                    @JoinColumn(name = "c_group"),
            },
            inverseJoinColumns = @JoinColumn(name = "c_permission")
    )
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "c_title")
    public I18NString getTitle() {
        return title;
    }

    public void setTitle(I18NString title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "groups")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @ManyToOne
    @JoinColumn(name = "c_status")
    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus groupStatus) {
        this.status = groupStatus;
    }

    @Override
    public String toString() {
        if (title!=null)
            return title.value();
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return code.equals(group.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
