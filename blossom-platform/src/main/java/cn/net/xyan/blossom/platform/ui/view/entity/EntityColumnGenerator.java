package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by zarra on 16/6/4.
 */
public class EntityColumnGenerator<E> implements Table.ColumnGenerator {

    JPAContainer<E> jpaContainer;
    String   path;

    EntityViewService entityViewService;

    public EntityColumnGenerator(EntityViewService entityViewService,JPAContainer<E> container,String path){
        this.entityViewService = entityViewService;
        this.jpaContainer = container;
        this.path = path;
    }

    public <T> Component generateCell(T cellValue){
        Class<T> tClass = (Class<T>) cellValue.getClass();
        if (cellValue instanceof Map)
            throw new UnsupportedOperationException("Map is not support");
        else if (cellValue instanceof Collection){
            Collection<?> c = (Collection<?>) cellValue;
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            //horizontalLayout.setSpacing(true);
            for (Object obj : c){
                Component cell = generateCell(obj);
                if (cell!=null){
                    horizontalLayout.addComponent(cell);
                }
            }
            return horizontalLayout;
        }else {
            EntityRenderConfiguration<T> entityRenderConfiguration = entityViewService.entityRenderConfiguration(tClass);
            if (entityRenderConfiguration!=null){
                return entityRenderConfiguration.tableDisplayComponent(cellValue);
            }
        }
        return  null;
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {

        EntityItem<E> entityItem = jpaContainer.getItem(itemId);

        List<String> propertyNames = Arrays.asList(path.split("\\."));

        Object value = null;

        if (propertyNames.size() > 0){
            String pName = propertyNames.get(0);
            EntityItemProperty property = entityItem.getItemProperty(pName);
            value = property.getValue();
            if (propertyNames.size() >1){
                List<String> newPropertyNames = propertyNames.subList(1,propertyNames.size());
                value = ReflectUtils.getProperty(value,newPropertyNames);
            }
        }


        //E entity = entityItem.getEntity();

        //Object fieldValue = ReflectUtils.getProperty(entity,path);

        return generateCell(value);
    }
}
