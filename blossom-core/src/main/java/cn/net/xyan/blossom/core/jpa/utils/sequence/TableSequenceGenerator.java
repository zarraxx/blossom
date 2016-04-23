package cn.net.xyan.blossom.core.jpa.utils.sequence;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.beans.BeanUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by xiashenpin on 16/1/20.
 */
public class TableSequenceGenerator extends TableGenerator {

    public static final String STRATEGY = "cn.net.xyan.blossom.core.jpa.utils.sequence.TableSequenceGenerator";

    private DecimalFormat format;

    private Class<? extends ISequenceFormat> formatClass;


    /**
     * read configuration parameters
     */
    @Override
    public void configure(
            Type type, Properties params, Dialect dialect) {

        params.put(TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY,true);

        super.configure(LongType.INSTANCE, params, dialect);


        String formatPattern = params.getProperty("format");
        if (formatPattern != null)
            format = new DecimalFormat(formatPattern);

        String formatClass_ = params.getProperty("formatClass");
        if (formatClass_ != null) {
            try {
                formatClass = (Class<? extends ISequenceFormat>) Class.forName(formatClass_);
            } catch (ClassNotFoundException e) {
                throw new StatusAndMessageError(-99,"class:"+formatClass_+" not found");
            }
        }
    }

    protected Serializable formatGenerated(Long generated, Object object) {
        try {
            List<String> names = new LinkedList<>();
            List<Object> values = new LinkedList<>();
            List<String> propertyNames = ReflectUtils.propertyNameWithAnnotation(object.getClass(), Id.class, GeneratedValue.class, GenericGenerator.class);
            if (propertyNames.size() != 1) {
                throw new StatusAndMessageError(-99, "can't find id property name.");
            }
            String pkName = propertyNames.get(0);
            Class<?> pkClass = null;
            for (PropertyDescriptor propertyDescriptorse : BeanUtils.getPropertyDescriptors(object.getClass())) {
                String name = propertyDescriptorse.getName();
                Object value = ReflectUtils.getProperty(object, propertyDescriptorse);
                if (pkClass== null && pkName != null && pkName.equals(name)) {
                    pkClass = propertyDescriptorse.getPropertyType();
                }
                names.add(name);
                values.add(value);
            }

            if (pkClass == null || !Serializable.class.isAssignableFrom(pkClass)) {
                throw new StatusAndMessageError(-99, "can't find id property class.");
            }

            if (formatClass == null) {
                if (Number.class.isAssignableFrom(pkClass)) {
                    return AbstractSequenceFormat.long2Number(generated, (Class<? extends Number>) pkClass);
                }else{
                    return String.valueOf(generated);
                }
            }

            ISequenceFormat ISequenceFormat = formatClass.newInstance();

            return ISequenceFormat.formatSequence(
                    (Class<? extends Serializable>) pkClass,
                    generated,
                    values.toArray(new Object[0]),
                    names.toArray(new String[0]), pkName);

        } catch (Throwable e) {
            e.printStackTrace();
            return generated;
        }

    }


    /**
     * get super value and format it
     */
    @Override
    public synchronized Serializable generate(
            SessionImplementor session, Object object)
            throws HibernateException {
        Long generated = (Long) super.generate(session, object);

        if (format != null)
            return format.format(generated);
        else
            return formatGenerated(generated, object);


    }
}
