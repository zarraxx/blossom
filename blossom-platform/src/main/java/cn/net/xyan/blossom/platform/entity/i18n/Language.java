package cn.net.xyan.blossom.platform.entity.i18n;

import cn.net.xyan.blossom.platform.entity.Constant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Locale;

/**
 * Created by zarra on 16/5/14.
 */
@Entity
@Table( schema = Constant.Schema,name = "ui_language")
public class Language  {

    public final static Locale English = Locale.US;

    String title;

    String displayName;

    String localeDisplayName;

    boolean available;

    public Language(){

    }

    public Language(Locale locale){
        this(locale,true);
    }

    public Language(Locale locale,boolean available){

        setTitle(locale.toLanguageTag());
        setDisplayName(locale.getDisplayName(English));
        setLocaleDisplayName(locale.getDisplayName(locale));
        setAvailable(available);
    }

    @Id
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocaleDisplayName() {
        return localeDisplayName;
    }

    public void setLocaleDisplayName(String localeDisplayName) {
        this.localeDisplayName = localeDisplayName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Transient
    public Locale getLocale(){
        return Locale.forLanguageTag(getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        return title.equals(language.title);

    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
