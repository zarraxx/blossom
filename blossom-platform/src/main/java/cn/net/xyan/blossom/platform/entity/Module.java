package cn.net.xyan.blossom.platform.entity;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "ui_module")
public class Module {
    String code;
    I18NString title;

    String viewName;
    String viewClassName;

    List<Catalog> catalogs = new LinkedList<>();

    List<Permission> essentialPermission = new LinkedList<>();

    @Id
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "c_title")
    public I18NString getTitle() {
        return title;
    }

    public void setTitle(I18NString title) {
        this.title = title;
    }

    @Column(name = "c_view_class",unique = true)
    public String getViewClassName() {
        return viewClassName;
    }

    public void setViewClassName(String viewClassName) {
        this.viewClassName = viewClassName;
    }

    @Column(name = "c_view_name",unique = true)
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @OneToMany
    @JoinTable(name = "ui_module_permissions",
            joinColumns = {@JoinColumn(name = "c_module")},
            inverseJoinColumns = {@JoinColumn(name = "c_permission")}
    )
    public List<Permission> getEssentialPermission() {
        return essentialPermission;
    }

    public void setEssentialPermission(List<Permission> essentialPermission) {
        this.essentialPermission = essentialPermission;
    }

    @ManyToMany(mappedBy = "modules",cascade = {CascadeType.PERSIST})
    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

}
