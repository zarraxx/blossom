package cn.net.xyan.blossom.platform.entity.dict;

import cn.net.xyan.blossom.core.jpa.utils.sequence.AbstractSequenceFormat;
import cn.net.xyan.blossom.core.jpa.utils.sequence.TableSequenceGenerator;
import cn.net.xyan.blossom.platform.entity.Constant;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(schema = Constant.Schema,name = "sys_status_and_type",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type","index_value"}),
        @UniqueConstraint(columnNames = {"type","title"})
})
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type",discriminatorType = DiscriminatorType.STRING)
public abstract class StatusAndType {

    static public class StatusAndTypeIDFormat extends AbstractSequenceFormat{

        public static final String FormatClass = "cn.net.xyan.blossom.platform.entity.dict.StatusAndType$StatusAndTypeIDFormat";

        @Override
        public Serializable formatSequence(Class<? extends Serializable> returnClass, Object entity, Long sequence, Object[] propertyStates, String[] propertyNames, String propertyName) {
            DiscriminatorValue dv = entity.getClass().getAnnotation(DiscriminatorValue.class);
            if (dv != null){
                return String.format("%s_%04d",dv.value(),sequence);
            }else {
                return String.valueOf(sequence);
            }
        }
    }

    final static String StatusAndTypeID = "cn.net.xyan.blossom.platform.entity.dict.StatusAndTypeID";

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
    @Column(name = "statusID",unique = true,updatable = false,nullable = false,length = 32)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = StatusAndTypeID)
    @GenericGenerator(name = StatusAndTypeID,
            strategy = TableSequenceGenerator.STRATEGY,
            parameters = {
                    //@Parameter(name = "format", value = "LD0000000")
                    @org.hibernate.annotations.Parameter(name = "segment_value", value = "sys_status_and_type"),
                    @org.hibernate.annotations.Parameter(name = "formatClass", value = StatusAndTypeIDFormat.FormatClass)
            })
    public String getStatusId() {
        return uuid;
    }

    public void setStatusId(String uuid) {
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
    @CollectionTable(schema = Constant.Schema,name = "sys_status_and_type_ex")
    @MapKeyColumn(name = "ex_name")
    @Column(name = "ex_value")
    public Map<String, String> getExValues() {
        return exValues;
    }

    public void setExValues(Map<String, String> exValues) {
        this.exValues = exValues;
    }
}

