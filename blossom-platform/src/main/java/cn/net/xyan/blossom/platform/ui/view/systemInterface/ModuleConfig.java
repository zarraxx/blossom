package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.Module_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/6.
 */
public class ModuleConfig extends EntityRenderConfiguration<Module> {
    @Override
    public void configFormFiled() {
        addFormField(Module_.code);
        addFormField(Module_.viewClassName);

        addFormField(Module_.viewName);

        addFormField(Module_.sortOrder);

        addFormField(Module_.essentialPermission);

    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(Module_.code);
        addTableColumn(Module_.viewClassName);

        addTableColumn(Module_.viewName);

        addTableColumn(Module_.catalogs);

        addTableColumn(Module_.essentialPermission);

        addTableColumn(Module_.sortOrder);
    }
}
