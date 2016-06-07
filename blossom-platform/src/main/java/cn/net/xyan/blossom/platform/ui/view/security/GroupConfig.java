package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Group_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/6.
 */
public class GroupConfig extends EntityRenderConfiguration<Group> {
    @Override
    public void configFormFiled() {
        addFormField(Group_.code);

        addFormField(Group_.status);

        addFormField(Group_.permissions);
    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(Group_.code);

        addTableColumn(Group_.title);

        addTableColumn(Group_.describe);

        addTableColumn(Group_.status);

        addTableColumn(Group_.permissions);
    }
}
