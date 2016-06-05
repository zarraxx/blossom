package cn.net.xyan.blossom.platform.entity;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "ui_module")
public class Module extends ComparableEntity<Module>{
    String code;
    I18NString title;

    String viewName;
    String viewClassName;

    SortedSet<Catalog> catalogs = new TreeSet<>();

    SortedSet<Permission> essentialPermission = new TreeSet<>();

    public static String moduleMessageKey(String code){
        return String.format("ui.module.%s.title", code);
    }

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
    //@OrderBy("name ASC")
    @SortNatural
    public SortedSet<Permission> getEssentialPermission() {
        return essentialPermission;
    }

    public void setEssentialPermission(SortedSet<Permission> essentialPermission) {
        this.essentialPermission = essentialPermission;
    }

    @ManyToMany(mappedBy = "modules",cascade = {CascadeType.PERSIST})
    //@OrderBy("name ASC")
    @SortNatural
    public SortedSet<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(SortedSet<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    @Override
    public int compareTo(Module o) {
        int value =  super.compareTo(o);
        if (value == 0){
            value =  getCode().compareTo(o.getCode());
        }

        return value;
    }
}
