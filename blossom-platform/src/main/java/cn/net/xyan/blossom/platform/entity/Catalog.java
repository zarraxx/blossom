package cn.net.xyan.blossom.platform.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "ui_catalog")
public class Catalog {
    String code;
    String title;
    String describe;

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
}
