package cn.net.xyan.blossom.plan.entity;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/6/16.
 */
@Entity
@DiscriminatorValue("contextParamType")
public class ContextParamType extends StatusAndType {

    public ContextParamType(){

    }

    public ContextParamType(Integer index,String title){
        super(index,title);
    }
}
