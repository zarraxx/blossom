package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.Permission_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/4.
 */
public class PermissionConfig extends EntityRenderConfiguration<Permission> {

    @Override
    public void configFormFiled() {
        addFormField(Permission_.code);
       // addFormField(Permission_.title);
    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(Permission_.code);
        addTableColumn(Permission_.title);
        addTableColumn(Permission_.describe);
        addTableColumn(Permission_.groups);
    }
}
