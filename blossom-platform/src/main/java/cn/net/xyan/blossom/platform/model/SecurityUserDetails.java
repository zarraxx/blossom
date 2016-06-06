package cn.net.xyan.blossom.platform.model;

import cn.net.xyan.blossom.platform.entity.security.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by zarra on 16/6/6.
 */
public class SecurityUserDetails extends org.springframework.security.core.userdetails.User {
    private User user;

    public SecurityUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getLoginName(), user.getPassword(), authorities);
        this.user = user;
    }

    public SecurityUserDetails(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getLoginName(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
