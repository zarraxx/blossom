package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

/**
 * Created by zarra on 16/6/4.
 */
public class EntityColumnGenerator<E> implements Table.ColumnGenerator {

    JPAContainer<E> jpaContainer;
    String   path;

    EntityViewService entityViewService;

    TableValueConverter<?> converter;

    public EntityColumnGenerator(EntityViewService entityViewService,JPAContainer<E> container,String path,TableValueConverter<?> converter){
        this.entityViewService = entityViewService;
        this.jpaContainer = container;
        this.path = path;

        this.converter = converter;
    }

    public <C extends Collection<V>,V> Component generateCellForCollection(C cellValue,TableValueConverter<? super  V> converter){

        Class<C> tClass = (Class<C>) cellValue.getClass();
        if (cellValue instanceof Map)
            throw new UnsupportedOperationException("Map is not support");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        for (V value  : cellValue){
            Component cell = generateCellForValue( value,converter);
            if (cell!=null){
                horizontalLayout.addComponent(cell);
            }
        }
        return horizontalLayout;
    }

    public <T> Component generateCellForValue(T cellValue,TableValueConverter<? super  T> converter){
        Class<T> tClass = (Class<T>) cellValue.getClass();

        EntityRenderConfiguration<T> entityRenderConfiguration = entityViewService.entityRenderConfiguration(tClass);

        if (entityRenderConfiguration!=null){
            return entityRenderConfiguration.tableDisplayComponent(cellValue);
        }else {
            String value = cellValue.toString();
            if (converter!=null) {
                Locale locale = LocaleContextHolder.getLocale();
                value = converter.convertToPresentation(cellValue, String.class, locale);
            }

            Label label = new Label(value);
            label.setValue(value);

            return label;
        }

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

        if (value == null){
            return new Label();
        }
        if (value instanceof Collection){
            Collection c = (Collection) value;
            Component cp =
                    this.<Collection,Object>generateCellForCollection(c, (TableValueConverter<? super Object>) converter);
            return cp;
        }else{
            return generateCellForValue(value, (TableValueConverter<? super Object>) converter);
        }


        //E entity = entityItem.getEntity();

        //Object fieldValue = ReflectUtils.getProperty(entity,path);


    }
}
