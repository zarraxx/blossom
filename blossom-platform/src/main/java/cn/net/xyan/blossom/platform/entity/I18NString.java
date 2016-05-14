package cn.net.xyan.blossom.platform.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(schema = "blossom",name ="tb_i18n")
public class I18NString {
    String key;

    String defaultValue;


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
}
