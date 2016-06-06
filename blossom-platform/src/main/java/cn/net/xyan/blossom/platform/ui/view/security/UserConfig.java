package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.entity.security.User_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/6.
 */
public class UserConfig extends EntityRenderConfiguration<User> {

    @Override
    public void configFormFiled() {
        addFormField(User_.loginName);
        addFormField(User_.realName);
        addFormField(User_.password);

        addFormField(User_.permissions);
        addFormField(User_.groups);

    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(User_.loginName);
        addTableColumn(User_.realName);
        addTableColumn(User_.password);

        addTableColumn(User_.permissions);
        addTableColumn(User_.groups);

        addTableColumn(User_.createDate);
        addTableColumn(User_.lastLogin);
    }
}
