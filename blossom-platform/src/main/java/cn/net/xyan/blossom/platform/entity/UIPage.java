package cn.net.xyan.blossom.platform.entity;

import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by zarra on 16/5/13.
 *
 *
 * Page -> Catalog -> Module
 */
@Entity
@Table(name = "ui_page")
public class UIPage extends ComparableEntity<UIPage>{
    String code;
    String title;

    String uiClassName;

    SortedSet<Catalog> catalogs = new TreeSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(name = "ui_page_catalog"
            ,joinColumns = @JoinColumn(name = "c_page")
            ,inverseJoinColumns = @JoinColumn(name = "c_catalog")
    )
    @OrderColumn(name="c_index")
    //@OrderBy("name ASC")
    @SortNatural
    public SortedSet<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(SortedSet<Catalog> catalogs) {
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

    @Override
    public int compareTo(UIPage o) {
        int value =  super.compareTo(o);
        if (value == 0){
            value =  getCode().compareTo(o.getCode());
        }

        return value;
    }

    @Override
    public String toString() {
        if (title!=null)
            return title;
        return getCode();
    }
}
