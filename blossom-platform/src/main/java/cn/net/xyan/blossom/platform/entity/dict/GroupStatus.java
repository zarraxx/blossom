package cn.net.xyan.blossom.platform.entity.dict;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/6/6.
 */
@Entity
@DiscriminatorValue("groupStatus")
public class GroupStatus extends StatusAndType{
    public GroupStatus(){

    }

    public GroupStatus(Integer index,String title){
        super(index,title);
    }
}
