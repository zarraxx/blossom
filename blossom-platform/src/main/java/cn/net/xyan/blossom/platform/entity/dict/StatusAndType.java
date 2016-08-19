package cn.net.xyan.blossom.platform.entity.dict;

import cn.net.xyan.blossom.core.jpa.utils.sequence.AbstractSequenceFormat;
import cn.net.xyan.blossom.core.jpa.utils.sequence.TableSequenceGenerator;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import org.hibernate.annotations.*;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by zarra on 16/5/13.
 */
@Entity
@Table(name = "sys_status_and_type",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type","index_value"}),
        @UniqueConstraint(columnNames = {"type","c_title"})
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

    final static String StatusAndTypeID = "cn.net.xyan.blossom.platform.entity.dict.StatusAndType";

    private String uuid;

    private Integer index;
    private I18NString title;

    private Boolean abandon;

    private String type;

    private Map<String,String> exValues;

    public StatusAndType(){

    }

    public static String statusAndTypeTitleKey(@Nonnull  StatusAndType st, @Nonnull Integer index){
        Class<? extends  StatusAndType> cls = st.getClass();

        return String.format("status.%s.%d",cls.getName(),index);
    }

    public StatusAndType(Integer index,String  title){
        this(index,title,false);
    }

    public StatusAndType(Integer index,String  title,Boolean abandon){
        this.index = index;
        this.title = new I18NString(statusAndTypeTitleKey(this,index),title);
        this.abandon = abandon;
    }


    @Id
    @Column(name = "statusID",unique = true,updatable = false,nullable = false,length = 32)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = StatusAndTypeID)
    @GenericGenerator(name = StatusAndTypeID,
            strategy = TableSequenceGenerator.STRATEGY,
            parameters = {
                    //@Parameter(name = "format", value = "LD0000000")
                    @org.hibernate.annotations.Parameter(name = "segment_value", value = StatusAndTypeID),
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

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "c_title")
    public I18NString getTitle() {
        return title;
    }

    public void setTitle(I18NString title) {
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sys_status_and_type_ex")
    @MapKeyColumn(name = "ex_name")
    @Column(name = "ex_value")
    public Map<String, String> getExValues() {
        return exValues;
    }

    public void setExValues(Map<String, String> exValues) {
        this.exValues = exValues;
    }

    @Override
    public String toString() {
        if (getTitle()!=null)
            return getTitle().value();
        return String.format("%s-%d",getClass().getSimpleName(),getIndex());
    }
}

