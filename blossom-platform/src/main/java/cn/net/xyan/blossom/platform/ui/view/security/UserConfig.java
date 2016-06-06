package cn.net.xyan.blossom.platform.ui.view.security;

import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.entity.security.User_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditFrom;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

import java.util.Map;

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

        addFormField(User_.status).addFormFieldAfterBind(new FormFieldConfig.FormFieldSetup() {
            @Override
            public void fieldSetup(AbstractField field, EntityEditFrom<?> parent, AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {



                AbstractSelect abstractSelect = (AbstractSelect) field;
                abstractSelect.setNullSelectionAllowed(false);

                if (EntityEditFrom.FormStatus.Add == parent.getStatus()){

                    JPAContainer<UserStatus> container = (JPAContainer<UserStatus>) abstractSelect.getContainerDataSource();

                    Object itemID = container.getIdByIndex(0);

                    UserStatus userStatus = container.getItem(itemID).getEntity();

                    abstractSelect.setValue(itemID);

                }
            }
        });

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

        addTableColumn(User_.status);
    }
}
