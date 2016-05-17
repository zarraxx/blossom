package cn.net.xyan.blossom.platform.entity.dict;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/5/17.
 */
@Entity
@DiscriminatorValue("userStatus")
public class UserStatus extends StatusAndType {
    public UserStatus(){

    }

    public UserStatus(int index,String title){
        super(index,title);
    }
}
