package cn.net.xyan.blossom.declarative.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/8/20.
 */
@Entity
@Table(name = "sys_dynamic_bean")
public class DynamicBeanDefinition {
    String beanName;
    String beanInterfaceName;

    String attach;

    List<String> requiredBeanName = new LinkedList<>();

    Boolean singleton;

    Boolean abandon;

    @Column(name = "attach",length = Integer.MAX_VALUE)
    @Lob
    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getBeanInterfaceName() {
        return beanInterfaceName;
    }

    public void setBeanInterfaceName(String beanInterfaceName) {
        this.beanInterfaceName = beanInterfaceName;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sys_dynamic_bean_required_name",
            joinColumns = @JoinColumn(name = "beanName")
    )
    @OrderColumn(name="index")
    @Column(name = "requiredBeanName",length = 4000)
    public List<String> getRequiredBeanName() {
        return requiredBeanName;
    }

    public void setRequiredBeanName(List<String> requiredBeanName) {
        this.requiredBeanName = requiredBeanName;
    }

    public Boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public Boolean getAbandon() {
        return abandon;
    }

    public void setAbandon(Boolean abandon) {
        this.abandon = abandon;
    }

    @Id
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
