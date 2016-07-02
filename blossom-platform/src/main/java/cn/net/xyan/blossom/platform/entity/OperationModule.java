package cn.net.xyan.blossom.platform.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/7/2.
 */
@Entity
@DiscriminatorValue("action")
public class OperationModule extends Module {

    String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
