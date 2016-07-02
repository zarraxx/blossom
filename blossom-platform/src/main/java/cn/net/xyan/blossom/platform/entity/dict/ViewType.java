package cn.net.xyan.blossom.platform.entity.dict;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/7/2.
 */
@Entity
@DiscriminatorValue("viewType")
public class ViewType extends StatusAndType {
    public ViewType(){}
    public ViewType(Integer index,String title){super(index,title);}
}
