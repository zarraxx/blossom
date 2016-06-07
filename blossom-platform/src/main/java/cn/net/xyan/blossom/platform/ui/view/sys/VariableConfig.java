package cn.net.xyan.blossom.platform.ui.view.sys;

import cn.net.xyan.blossom.platform.entity.dict.SysVariable;
import cn.net.xyan.blossom.platform.entity.dict.SysVariable_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/7.
 */
public class VariableConfig extends EntityRenderConfiguration<SysVariable> {
    @Override
    public void configFormFiled() {
        addFormField(SysVariable_.uuid);
        addFormField(SysVariable_.title);
        addFormField(SysVariable_.value);
        addFormField(SysVariable_.valueClassName);
    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(SysVariable_.uuid);
        addTableColumn(SysVariable_.title);
        addTableColumn(SysVariable_.value);
        addTableColumn(SysVariable_.valueClassName);
    }
}
