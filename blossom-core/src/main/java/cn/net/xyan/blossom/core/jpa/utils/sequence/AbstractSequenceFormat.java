package cn.net.xyan.blossom.core.jpa.utils.sequence;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;

import javax.persistence.metamodel.Attribute;

/**
 * Created by xiashenpin on 16/1/20.
 */
public abstract class AbstractSequenceFormat implements ISequenceFormat {

   public static Number long2Number(Long value,Class<? extends Number> numberClass){
        String valueString = String.valueOf(value);
        if (Long.class.isAssignableFrom(numberClass)){
            return value;
        }
        else if (Integer.class.isAssignableFrom(numberClass)){
            return  Integer.valueOf(valueString);
        }else if (Float.class.isAssignableFrom(numberClass)){
            return  Float.valueOf(valueString);
        }else if (Double.class.isAssignableFrom(numberClass)){
            return  Double.valueOf(valueString);
        }else {
            return  value;
        }
    }

    protected Object findPropertyValue(Object[] propertyStates, String[] propertyNames, String propertyName){

        for (int i = 0; i < propertyNames.length; i++)
        {
            if (propertyName.equals(propertyNames[i]))
            {
                return  propertyStates[i];
            }
        }

        throw new StatusAndMessageError(-99,"can't find property name:"+propertyName);
    }

    protected <T,V,A extends Attribute<T,V>>  V findPropertyValue(Object[] propertyStates, String[] propertyNames, A attribute){

        String propertyName = attribute.getName();

        for (int i = 0; i < propertyNames.length; i++)
        {
            if (propertyName.equals(propertyNames[i]))
            {
                return  (V)propertyStates[i];
            }
        }

        throw new StatusAndMessageError(-99,"can't find property name:"+propertyName);
    }
}
