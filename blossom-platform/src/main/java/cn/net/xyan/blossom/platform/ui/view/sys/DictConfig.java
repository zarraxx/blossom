package cn.net.xyan.blossom.platform.ui.view.sys;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import cn.net.xyan.blossom.platform.entity.dict.StatusAndType_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditFrom;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;

import java.util.Map;

/**
 * Created by zarra on 16/6/7.
 */
public class DictConfig extends EntityRenderConfiguration<StatusAndType> {
    @Override
    public void configFormFiled() {
//        addFormField(StatusAndType_.type).setFormFieldAfterBind(new FormFieldConfig.FormFieldSetup() {
//            @Override
//            public void fieldSetup(AbstractField field, EntityEditFrom<?> parent, AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {
//                field.setReadOnly(true);
//            }
//        });
        addFormField(StatusAndType_.statusId);
        addFormField(StatusAndType_.index);
        addFormField(StatusAndType_.title);
        addFormField(StatusAndType_.abandon);
    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(StatusAndType_.type);
        addTableColumn(StatusAndType_.statusId);
        addTableColumn(StatusAndType_.index);
        addTableColumn(StatusAndType_.title);
        addTableColumn(StatusAndType_.abandon);
    }
}
