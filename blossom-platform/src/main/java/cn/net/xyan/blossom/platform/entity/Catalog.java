package cn.net.xyan.blossom.platform.entity;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import com.sun.org.apache.xml.internal.security.utils.I18n;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "ui_catalog")
public class Catalog {
    String code;
    I18NString title;
    String describe;

    List<UIPage> uiPages = new LinkedList<>();

    List<Module> modules = new LinkedList<>();

    List<Permission> essentialPermission = new LinkedList<>();

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

    @OneToMany
    @JoinTable(name = "ui_catalog_permissions",
            joinColumns = {@JoinColumn(name = "c_catalog")},
            inverseJoinColumns = {@JoinColumn(name = "c_permission")}
    )
    public List<Permission> getEssentialPermission() {
        return essentialPermission;
    }

    public void setEssentialPermission(List<Permission> essentialPermission) {
        this.essentialPermission = essentialPermission;
    }

    @ManyToMany(mappedBy = "catalogs",cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    public List<UIPage> getUiPages() {
        return uiPages;
    }

    public void setUiPages(List<UIPage> uiPages) {
        this.uiPages = uiPages;
    }

    @ManyToMany
    @JoinTable(name = "ui_catalog_modules"
            ,joinColumns = @JoinColumn(name = "c_catalog")
            ,inverseJoinColumns = @JoinColumn(name = "c_module")
    )
    @OrderColumn(name="c_index")
    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

}
