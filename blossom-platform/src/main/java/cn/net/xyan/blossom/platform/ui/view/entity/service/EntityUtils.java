package cn.net.xyan.blossom.platform.ui.view.entity.service;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/11.
 */
public class EntityUtils {
    static EntityManagerFactory emf;
    static Metamodel  metamodel;

    public static EntityManagerFactory entityManagerFactory(){
        if (emf == null){
            emf = ApplicationContextUtils.getBean(EntityManagerFactory.class);
        }
        return emf;
    }


    public static Metamodel metamodel(){
        if (metamodel == null){
            metamodel = entityManagerFactory().getMetamodel();
        }
        return metamodel;
    }

    public static Class<?> valueTypeForAttribute(Attribute<?, ?> attribute) {
        Class<?> valueType = attribute.getJavaType();

        if (attribute instanceof PluralAttribute) {
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) attribute;

            valueType = pluralAttribute.getElementType().getJavaType();
        }

        return valueType;
    }


    public static  List<Attribute<?, ?>> createAttributesFromStringList(EntityType<?> rootType, List<String> stringList) {
        List<Attribute<?, ?>> attributes = new LinkedList<>();

        EntityType<?> entityType = rootType;

        for (String attributesName : stringList) {
            try {
                Attribute<?, ?> attribute = entityType.getAttribute(attributesName);

                attributes.add(attribute);

                Class<?> valueType = valueTypeForAttribute(attribute);

                entityType = metamodel.entity(valueType);
            } catch (Throwable e) {
                throw new StatusAndMessageError(-9, e);
            }
        }

        return attributes;
    }



    public static Path<?> pathForAttributes(List<Attribute<?, ?>> attributes, From<?, ?> from) {
        From<?, ?> innerForm = from;
        if (attributes.size() > 0) {
            Attribute<?, ?> attribute = attributes.get(0);
            if (attributes.size() == 1) {
                if (attribute instanceof PluralAttribute){
                    PluralAttribute pluralAttribute = (PluralAttribute) attribute;
                    innerForm = innerForm.join(pluralAttribute.getName(),JoinType.LEFT);
                    Join<?,?> join = (Join<?, ?>) innerForm;
                    return join;
                   // return innerForm.get(attribute.getName());
                }else {
                    return innerForm.get(attribute.getName());
                }
            } else {
                innerForm = innerForm.join(attribute.getName(), JoinType.LEFT);
                List<Attribute<?, ?>> newAttributes = attributes.subList(1, attributes.size());
                return pathForAttributes(newAttributes, innerForm);
            }
        } else {
            return null;
        }
    }

    public static <E> List< EntityType<? extends  E> > allEntityTypeInheritClass(Class<E> entityCls){
        List< EntityType<? extends  E>  >result = new LinkedList<>();

        EntityType<E> entityType = metamodel().entity(entityCls);

        for( EntityType<?> type :metamodel().getEntities() ){
            Class<?> eCls = type.getJavaType();

            if (eCls.getSuperclass().equals( entityCls)){
                result.add((EntityType<? extends E>) type);
            }

        }


        return result;
    }
}
