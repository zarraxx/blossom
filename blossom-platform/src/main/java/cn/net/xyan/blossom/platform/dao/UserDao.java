package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.security.User;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zarra on 16/5/30.
 */
public interface UserDao  extends EasyJpaRepository<User,String>{

    @Query("select  count(u) from User u inner join u.status us left join u.groups g  left join g.status gs  " +
            " where u.loginName = ?1 and ( us.abandon = true or gs.abandon = true)")
    int isUserBlocked(String username);
}
