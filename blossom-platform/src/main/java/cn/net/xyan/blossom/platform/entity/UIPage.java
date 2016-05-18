package cn.net.xyan.blossom.platform.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 *
 *
 * Page -> Catalog -> Module
 */
@Entity
@Table(name = "ui_page")
public class UIPage {
    String code;
    String title;

    List<Catalog> catalogs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany
    @JoinTable(name = "ui_page_catalog")
    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
