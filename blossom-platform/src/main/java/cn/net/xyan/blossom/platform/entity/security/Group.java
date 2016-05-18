package cn.net.xyan.blossom.platform.entity.security;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_group")
public class Group {
    String code;
    String title;
    String describe;
    List<Permission> permissions;

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

    @ManyToMany
    @JoinTable(name = "sys_group_permission",
            joinColumns = {
                    @JoinColumn(name = "c_group"),
            },
            inverseJoinColumns = @JoinColumn(name = "c_permission")
    )
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(mappedBy = "groups")
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
