package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.platform.entity.Catalog;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;

/**
 * Created by zarra on 16/6/4.
 */
public class CatalogConfig extends EntityRenderConfiguration<Catalog> {

    @Override
    public String getEntityTitle(Catalog entity) {
        return entity.getTitle().value();
    }
}
