package cn.net.xyan.blossom.platform.entity;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.*;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "ui_catalog")
public class Catalog extends ComparableEntity<Catalog>{
    String code;
    I18NString title;
    String describe;

    SortedSet<UIPage> uiPages = new TreeSet<>();

    SortedSet<Module> modules = new TreeSet<>();

    Set<Permission> essentialPermission = new HashSet<>();

    public static String catalogMessageKey(String code){
        return String.format("ui.catalog.%s.title", code);
    }

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "c_title")
    public I18NString getTitle() {
        return title;
    }

    public void setTitle(I18NString title) {
        this.title = title;
    }

    @ManyToMany
    @JoinTable(name = "ui_catalog_permissions",
            joinColumns = {@JoinColumn(name = "c_catalog")},
            inverseJoinColumns = {@JoinColumn(name = "c_permission")}
    )
    @OrderColumn(name="c_index")
    public Set<Permission> getEssentialPermission() {
        return essentialPermission;
    }

    public void setEssentialPermission(Set<Permission> essentialPermission) {
        this.essentialPermission = essentialPermission;
    }

    @ManyToMany(mappedBy = "catalogs"/*,cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH}*/)
    //@OrderBy("name ASC")
    @SortNatural
    public SortedSet<UIPage> getUiPages() {
        return uiPages;
    }

    public void setUiPages(SortedSet<UIPage> uiPages) {
        this.uiPages = uiPages;
    }

    @ManyToMany
    @JoinTable(name = "ui_catalog_modules"
            ,joinColumns = @JoinColumn(name = "c_catalog")
            ,inverseJoinColumns = @JoinColumn(name = "c_module")
    )
    @OrderColumn(name="c_index")
    //@OrderBy("name ASC")
    @SortNatural
    public SortedSet<Module> getModules() {
        return modules;
    }

    public void setModules(SortedSet<Module> modules) {
        this.modules = modules;
    }

    @Override
    public int compareTo(Catalog o) {
        int value =  super.compareTo(o);
        if (value == 0)
            value =  getCode().compareTo(o.getCode());

        return value;
    }

    @Override
    public String toString() {
        if (title!=null)
            return title.value();
        return getCode();
    }
}
