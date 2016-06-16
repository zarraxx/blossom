package cn.net.xyan.blossom.plan.entity;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/6/16.
 */
@Entity
@DiscriminatorValue("trigger")
public class TriggerType extends StatusAndType {

    public TriggerType(){

    }

    public TriggerType(Integer index,String title){
        super(index,title);
    }
}
