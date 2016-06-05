package cn.net.xyan.blossom.platform.ui.view.entity.service;

import cn.net.xyan.blossom.platform.service.Installer;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditFrom;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import cn.net.xyan.blossom.platform.ui.view.entity.filter.EntityFilterForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Component;

import javax.annotation.Nonnull;

/**
 * Created by zarra on 16/6/4.
 */
public interface EntityViewService extends Installer{
    void addScanPackage(@Nonnull String packageName);
    <E> void  setupEntityViewTable(@Nonnull EntityView<E> entityView);
    <E> EntityRenderConfiguration<E> entityRenderConfiguration(@Nonnull Class<E> eClass);

    <E> EntityEditFrom<E> createEntityForm(@Nonnull E  entity, @Nonnull EntityEditFrom.FormStatus status);

    <E> EntityEditFrom<E> createEntityForm(@Nonnull EntityItem<E> entityItem, @Nonnull EntityEditFrom.FormStatus status);

    <E> EntityFilterForm<E> createEntityFilter(@Nonnull EntityView<E> entityView);
}
