package cn.net.xyan.blossom.platform.entity.dict;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(schema = "blossom",name = "tb_sys_status_and_type",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type","index_value"}),
        @UniqueConstraint(columnNames = {"type","title"})
})
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type",discriminatorType = DiscriminatorType.STRING)
public abstract class StatusAndType {

    private String uuid;

    private Integer index;
    private String  title;

    private Boolean abandon;

    private String type;

    private Map<String,String> exValues;

    public StatusAndType(){

    }

    public StatusAndType(Integer index,String  title){
        this(index,title,false);
    }

    public StatusAndType(Integer index,String  title,Boolean abandon){
        this.index = index;
        this.title = title;
        this.abandon = abandon;
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

    @Column(name = "index_value")
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAbandon() {
        return abandon;
    }

    public void setAbandon(Boolean abandon) {
        this.abandon = abandon;
    }

    @Column(name = "type",insertable = false,updatable = false)
    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    @ElementCollection
    @CollectionTable(name = "t_sys_status_and_type_ex")
    @MapKeyColumn(name = "ex_name")
    @Column(name = "ex_value")
    public Map<String, String> getExValues() {
        return exValues;
    }

    public void setExValues(Map<String, String> exValues) {
        this.exValues = exValues;
    }
}

