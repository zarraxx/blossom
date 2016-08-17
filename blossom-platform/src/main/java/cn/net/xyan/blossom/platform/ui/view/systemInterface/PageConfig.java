package cn.net.xyan.blossom.platform.ui.view.systemInterface;


import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.platform.entity.Catalog_;
import cn.net.xyan.blossom.platform.entity.UIPage;

import cn.net.xyan.blossom.platform.entity.UIPage_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditForm;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

import cn.net.xyan.blossom.platform.ui.view.entity.TableValueConverter;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * Created by zarra on 16/6/4.
 */
public class PageConfig extends EntityRenderConfiguration<UIPage> {

    Logger logger = LoggerFactory.getLogger(PageConfig.class);

    @Override
    public void configTableColumnHeader() {
        addTableColumn(UIPage_.code).setDisplayTitle("Code");
        addTableColumn(UIPage_.title).setDisplayTitle("Title");
        addTableColumn(UIPage_.uiClassName).setDisplayTitle("UiClass").setConverter(new TableValueConverter<String>() {
            @Override
            public String doConvert(String value, Locale locale) {
                return "Class:"+value;
            }
        });

        addTableColumn(UIPage_.catalogs).setDisplayTitle("Catalogs");

        addTableColumn(UIPage_.sortOrder).setDisplayTitle("SortOrder");
    }

    @Override
    public void configFormFiled() {
        addFormField(UIPage_.code);
        addFormField(UIPage_.title);
        addFormField(UIPage_.uiClassName);
        addFormField(UIPage_.sortOrder).addFormFieldSetup(new FormFieldConfig.FormFieldSetup() {
            @Override
            public void fieldSetup(AbstractField field, EntityEditForm<?> parent, AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {
                NumberField numberField = (NumberField) field;

                numberField.setNullRepresentation("");
            }
        });
        addFormField(UIPage_.catalogs).addFormFieldSetup(new FormFieldConfig.FormFieldSetup() {
            @Override
            public void fieldSetup(AbstractField field, EntityEditForm<?> parent, AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {
                logger.info("hello world");
            }
        });
    }

    @Override
    public void configSpecification() {
        addFilter(JPA.Operator.Equal,UIPage_.catalogs, Catalog_.code);
        addFilter(JPA.Operator.Equal,UIPage_.catalogs);
        addFilter(JPA.Operator.Equal,UIPage_.code);
    }
}
