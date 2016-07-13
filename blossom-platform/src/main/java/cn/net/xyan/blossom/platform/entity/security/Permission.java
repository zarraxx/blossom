package cn.net.xyan.blossom.platform.entity.security;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_permission")
public class Permission {
    String code;
    I18NString title;
    I18NString describe;

    Set<Group> groups = new HashSet<>();
    Set<User> users = new HashSet<>();

    public static String i18nTitleKey(Permission permission){
        return String.format("permission.title.%s",permission.getCode());
    }

    public static String i18nDescribeKey(Permission permission){
        return String.format("permission.describe.%s",permission.getCode());
    }

    public Permission(){

    }

    public Permission(String code){
        setCode(code);
    }

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;

       // I18NString title = new I18NString(i18nTitleKey(this),code);
        //I18NString describe = new I18NString(i18nDescribeKey(this),"");

        //this.title = title;
       // this.describe = describe;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "c_describe")
    public I18NString getDescribe() {
        return describe;
    }

    public void setDescribe(I18NString describe) {
        this.describe = describe;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "c_title")
    public I18NString getTitle() {
        return title;
    }

    public void setTitle(I18NString title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "permissions")
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @ManyToMany(mappedBy = "permissions")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        if (title!=null){
            return title.value();
        }else{
            return code;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
