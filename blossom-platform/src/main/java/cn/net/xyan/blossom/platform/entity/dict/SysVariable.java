package cn.net.xyan.blossom.platform.entity.dict;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.JsonUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by zarra on 16/6/7.
 */
@Entity
@Table(name = "variable")
public class SysVariable {
    String uuid;
    String title;
    String describe;
    String value;
    String valueClassName;

    public SysVariable(){

    }

    public SysVariable(String title,Object value){
        this.title = title;
        if (value!=null){
            try {
                this.value = JsonUtils.objectMapper().writeValueAsString(value);
                this.valueClassName = value.getClass().getCanonicalName();
            } catch (Exception e) {
                throw new StatusAndMessageError(-99,e);
            }
        }
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "key",unique = true,nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueClassName() {
        return valueClassName;
    }

    public void setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
    }
}
