package cn.net.xyan.spring.boot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by zarra on 16/4/14.
 */
@Entity
public class SystemUser {
    String username;
    String password;

    @Id
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
