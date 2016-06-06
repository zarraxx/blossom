package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Catalog;

import cn.net.xyan.blossom.platform.entity.Catalog_;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString_;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import cn.net.xyan.blossom.platform.ui.view.entity.TableValueConverter;

import java.util.Locale;


/**
 * Created by zarra on 16/6/4.
 */
public class CatalogConfig extends EntityRenderConfiguration<Catalog> {

    @Override
    public String getEntityTitle(Catalog entity) {
        return entity.getTitle().value();
    }

    @Override
    public void configFormFiled() {
        addFormField(Catalog_.code);
        addFormField(Catalog_.title);
        addFormField(Catalog_.sortOrder);
        addFormField(Catalog_.modules);
        addFormField(Catalog_.essentialPermission);
    }

    @Override
    public void configTableColumnHeader() {
        addTableColumn(Catalog_.code);
        addTableColumn(Catalog_.title);
        addTableColumn(Catalog_.title, I18NString_.key).setConverter(new TableValueConverter<String>() {
            @Override
            public String doConvert(String value, Locale locale) {
                return "I18N Key:"+value;
            }
        });

        addTableColumn(Catalog_.uiPages);

        addTableColumn(Catalog_.modules);

        addTableColumn(Catalog_.essentialPermission);

        addTableColumn(Catalog_.sortOrder);


    }
}
