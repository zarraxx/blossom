package cn.net.xyan.blossom.platform.entity.i18n;


import cn.net.xyan.blossom.platform.entity.Constant;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(schema = Constant.Schema,name ="i18n")
public class I18NString {
    String key;

    String defaultValue;

    Map<Language,String> values = new HashMap<>();


    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Id
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    //@ElementCollection
    @CollectionTable(schema = Constant.Schema,name = "i18n_values")
    @MapKeyColumn(name = "ex_lang")
    @Column(name = "ex_value")
    public Map<Language, String> getValues() {
        return values;
    }


    public void setValues(Map<Language, String> values) {
        this.values = values;
    }

    public I18NString(){

    }

    public I18NString(String key,String defaultValue){
        setKey(key);
        setDefaultValue(defaultValue);
    }

    public void putValue(Language lang,String value){
        getValues().put(lang,value);
    }

    public void putValue(Locale locale,String value){
        Language language = new Language(locale);
        getValues().put(language,value);
    }

    public String value(Language lang){
        String v = getValues().get(lang);
        if (v != null)
            return v;
        else
            return getDefaultValue();
    }

    public String value(Locale locale){
        Language language = new Language(locale);
        return value(language);
    }

    public String value(){
        Locale locale = LocaleContextHolder.getLocale();
        return value(locale);
    }
}
