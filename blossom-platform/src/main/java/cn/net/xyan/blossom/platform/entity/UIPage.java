package cn.net.xyan.blossom.platform.entity;

import javax.persistence.*;
import java.util.LinkedList;
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

    String uiClassName;

    List<Catalog> catalogs = new LinkedList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany
    @JoinTable(name = "ui_page_catalog"
            ,joinColumns = @JoinColumn(name = "c_page")
            ,inverseJoinColumns = @JoinColumn(name = "c_catalog")
    )
    @OrderColumn(name="c_index")
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

    @Column(name = "c_ui_class",unique = true)
    public String getUiClassName() {
        return uiClassName;
    }

    public void setUiClassName(String uiClassName) {
        this.uiClassName = uiClassName;
    }
}
