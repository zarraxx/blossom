package cn.net.xyan.blossom.core.jpa.utils.sequence;


import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.jpa.annotation.Sequence;
import cn.net.xyan.blossom.core.jpa.entity.SequenceNumber;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import org.hibernate.StatelessSession;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiashenpin on 16/1/18.
 */
public class SequenceListener implements PreInsertEventListener,InitializingBean
{
    private static final long serialVersionUID = 7946581162328559098L;
    private final static Logger log = LoggerFactory.getLogger(SequenceListener.class);

    private EntityManagerFactory entityManagerFactory;

    private final Map<String, CacheEntry> cache = new HashMap<>();


    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }

    public void selfRegister()
    {
        // As you might expect, an EventListenerRegistry is the place with which event listeners are registered
        // It is a service so we look it up using the service registry
        //final EventListenerRegistry eventListenerRegistry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);

        HibernateEntityManagerFactory hibernateEntityManagerFactory = (HibernateEntityManagerFactory) this.entityManagerFactory;
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) hibernateEntityManagerFactory.getSessionFactory();
        EventListenerRegistry registry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
        //registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(listener);
        //registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(listener);

        // add the listener to the end of the listener chain
        registry.appendListeners(EventType.PRE_INSERT, this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        selfRegister();
    }

    @Override
    public boolean onPreInsert(PreInsertEvent p_event)
    {
        updateSequenceValue(p_event.getEntity(), p_event.getState(), p_event.getPersister().getPropertyNames());

        return false;
    }

    private void updateSequenceValue(Object p_entity, Object[] p_state, String[] p_propertyNames)
    {
        try
        {
            List<Field> fields = ReflectUtils.fields(p_entity.getClass(), Sequence.class);

            List<Method> methods = ReflectUtils.methods(p_entity.getClass(),Sequence.class);

            //优先方法注解
            if (!methods.isEmpty()){
                if (log.isDebugEnabled())
                {
                    log.debug("Intercepted custom sequence entity.");
                }
                for (Method method:methods){
                    if (log.isDebugEnabled())
                    {
                        log.debug("Intercepted custom sequence entity.");
                    }
                    Sequence sequence = method.getAnnotation(Sequence.class);
                    Class<? extends Serializable> returnClass = (Class<? extends Serializable>) method.getReturnType();
                    Long value = getSequenceNumber(sequence);
                    String property = ReflectUtils.getPropertyName(method);
                    if (property!=null)
                        setPropertyState(p_entity,p_state, p_propertyNames, property, value,sequence,returnClass);
                }
            }
            else if (!fields.isEmpty())
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Intercepted custom sequence entity.");
                }

                for (Field field : fields)
                {
                    Sequence sequence = field.getAnnotation(Sequence.class);
                    Long value = getSequenceNumber(sequence);

                    field.setAccessible(true);
                    field.set(p_entity, value);
                    Class<? extends Serializable> returnClass = (Class<? extends Serializable>) field.getDeclaringClass();
                    setPropertyState(p_entity,p_state, p_propertyNames, field.getName(), value,sequence,returnClass);

                    if (log.isDebugEnabled())
                    {
                        log.debug(String.format("Set %s property to %s.",field.toString(), value.toString() ));
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to set sequence property.", e);
        }
    }

    private Long getSequenceNumber(Sequence sequence)
    {
        synchronized (cache)
        {
            String p_className = sequence.value();
            CacheEntry current = cache.get(p_className);

            // not in cache yet => load from database
            if ((current == null) || current.isEmpty())
            {
                boolean insert = false;

                HibernateEntityManagerFactory hibernateEntityManagerFactory = (HibernateEntityManagerFactory) this.entityManagerFactory;
                SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) hibernateEntityManagerFactory.getSessionFactory();

                StatelessSession session = sessionFactoryImpl.openStatelessSession();
                session.beginTransaction();

                SequenceNumber sequenceNumber = (SequenceNumber) session.get(SequenceNumber.class, p_className);

                // not in database yet => create new sequence
                if (sequenceNumber == null)
                {
                    sequenceNumber = new SequenceNumber();
                    sequenceNumber.setClassName(p_className);
                    sequenceNumber.setNextValue(sequence.start());
                    sequenceNumber.setIncrementValue(sequence.incrementValue());
                    sequenceNumber.setStep(sequence.step());
                    insert = true;
                }

                current = new CacheEntry(sequenceNumber.getNextValue() + sequenceNumber.getIncrementValue(), sequenceNumber.getNextValue(),sequenceNumber.getStep());
                cache.put(p_className, current);
                //if (current.isEmpty())
                    sequenceNumber.setNextValue(sequenceNumber.getNextValue() + sequenceNumber.getIncrementValue());

                if (insert)
                {
                    session.insert(sequenceNumber);
                }
                else
                {
                    session.update(sequenceNumber);
                }
                session.getTransaction().commit();
                session.close();
            }

            return current.next();
        }
    }

    private void setPropertyState(Object entity,Object[] propertyStates, String[] propertyNames, String propertyName, Object propertyState,Sequence annotation,Class<? extends Serializable> returnClass)
    {
        for (int i = 0; i < propertyNames.length; i++)
        {
            if (propertyName.equals(propertyNames[i]))
            {
                if (!annotation.format().isAssignableFrom(DoNothingSequenceFormat.class)){
                    try {
                        ISequenceFormat format = annotation.format().newInstance();
                        Serializable formatValue = format.formatSequence(returnClass,entity,(Long)propertyState,propertyStates,propertyNames,propertyName);
                        propertyStates[i] = formatValue;
                        propertyStates[i] = formatValue;
                    } catch (Exception e) {
                        throw new StatusAndMessageError(-99,e);
                    }

                }else {
                    if (Number.class.isAssignableFrom(returnClass)) {
                        propertyStates[i] = AbstractSequenceFormat.long2Number((Long)propertyState, (Class<? extends Number>) returnClass);
                    }else if(String.class.isAssignableFrom(returnClass)){
                        propertyStates[i] = String.valueOf(propertyState);
                    }
                }
                return;
            }
        }
    }



    private static class CacheEntry
    {
        private long current;
        private final long limit;
        private long step;

        public CacheEntry(final long p_limit, final long p_current,final long step)
        {
            current = p_current;
            limit = p_limit;
            this.step = step;
        }

        public Long next()
        {
            return current+=step;
        }

        public boolean isEmpty()
        {
            return current >= limit;
        }
    }
}