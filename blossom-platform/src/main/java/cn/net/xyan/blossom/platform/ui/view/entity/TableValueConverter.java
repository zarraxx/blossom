package cn.net.xyan.blossom.platform.ui.view.entity;

import com.vaadin.data.util.converter.Converter;
import net.jodah.typetools.TypeResolver;

import java.util.Locale;

/**
 * Created by zarra on 16/6/6.
 */
public abstract class TableValueConverter<V> implements Converter<String, V> {

    public abstract String doConvert(V value,Locale locale);

    Class<V> vClass;

    public TableValueConverter(){

    }

    public TableValueConverter(Class<V> vClass){
        this.vClass = vClass;
    }

    public Class<V> valueType() {
        if (vClass == null) {
            Class<? extends TableValueConverter> thisCls = this.getClass();

            Class<?>[] typeArgs = TypeResolver.resolveRawArguments(TableValueConverter.class, thisCls);
            if (typeArgs.length > 0) {
                vClass = (Class<V>) typeArgs[0];
            }
        }
        return vClass;
    }

    @Override
    public V convertToModel(String value, Class<? extends V> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(V value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return doConvert(value,locale);
    }

    @Override
    public Class<V> getModelType() {
        return valueType();
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
