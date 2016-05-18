package cn.net.xyan.blossom.platform.entity.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_permission")
public class Permission {
    String code;
    String title;
    String describe;

    List<Group> groups;
    List<User> users;

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "permissions")
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @ManyToMany(mappedBy = "permissions")
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
