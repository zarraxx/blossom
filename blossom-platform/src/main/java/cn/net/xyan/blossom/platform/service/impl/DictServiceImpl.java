package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.JsonUtils;
import cn.net.xyan.blossom.platform.dao.StatusDao;
import cn.net.xyan.blossom.platform.dao.SysVariableDao;
import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import cn.net.xyan.blossom.platform.entity.dict.SysVariable;
import cn.net.xyan.blossom.platform.service.DictService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zarra on 16/6/6.
 */
public class DictServiceImpl implements DictService,InitializingBean {

    @PersistenceUnit
    EntityManagerFactory emf;

    @Autowired
    StatusDao statusDao;

    @Autowired
    SysVariableDao variableDao;

    Metamodel metamodel ;

    Map<Class<? extends StatusAndType>,String> cache = new HashMap<>();

    public String typeNameForCls(Class<? extends  StatusAndType> cls){
        return cache.get(cls);
    }

    @Override
    public <ST extends StatusAndType> ST setupStatus(Class<ST> cls, Integer index, String title) {
        return setupStatus(cls,index,title,false);
    }

    @Override
    public <ST extends StatusAndType> ST setupStatus(Class<ST> cls, Integer index, String title, boolean abandon) {
        String type = typeNameForCls(cls);

        ST st = (ST) statusDao.findByTypeAndIndex(type,index);

        if (st == null){
            try {
                Constructor<ST> constructor = cls.getConstructor(Integer.class,String.class);
                st = constructor.newInstance(index,title);
                st.setAbandon(abandon);
                st = statusDao.saveAndFlush(st);
            } catch (Exception e) {
                throw new StatusAndMessageError(-1,e);
            }
        }

        return st;
    }

    @Override
    public <ST extends StatusAndType> ST findStatus(Class<ST> cls, Integer index) {
        String typeName = typeNameForCls(cls);
        return (ST) statusDao.findByTypeAndIndex(typeName,index);
    }

    @Override
    public <ST extends StatusAndType> ST findStatus(Class<ST> cls, String title) {
        String typeName = typeNameForCls(cls);
        return (ST) statusDao.findByTypeAndTitle(typeName,title);
    }

    @Override
    public <ST extends StatusAndType> List<ST> findStatus(Class<ST> cls) {
        String typeName = typeNameForCls(cls);
        return (List<ST>) statusDao.findByType(typeName);
    }

    @Override
    public Collection<Class<? extends StatusAndType>> allStatusType() {
        Set<Class<? extends StatusAndType>> result = new HashSet<>(cache.keySet());
        return result;
    }

    @Override
    @Transactional
    public SysVariable setupVariable(String key, Object value) {
        SysVariable sysVariable = variableDao.findByTitle(key);
        if (sysVariable == null){
            sysVariable = new SysVariable(key,value);
            sysVariable = variableDao.saveAndFlush(sysVariable);
        }
        return sysVariable;
    }

    @Override
    public SysVariable putVariable(String key, Object value) {
        SysVariable sysVariable = setupVariable(key,value);
        try {
            sysVariable.setValue(JsonUtils.objectMapper().writeValueAsString(value));
            sysVariable = variableDao.saveAndFlush(sysVariable);
            return sysVariable;
        } catch (JsonProcessingException e) {
            throw new StatusAndMessageError(-9,e);
        }
    }

    @Override
    public <T> T getVariable(Class<T> tClass, String key) {
        SysVariable sysVariable = variableDao.findByTitle(key);
        if (sysVariable!=null){
            String value = sysVariable.getValue();
            try {
                return JsonUtils.objectMapper().readValue(value,tClass);
            } catch (IOException e) {
                if (String.class.isAssignableFrom(tClass)){
                    return (T) value;
                }
                else if (Number.class.isAssignableFrom(tClass)){
                    try {
                        NumberFormat format = NumberFormat.getInstance();
                        return (T) format.parse(value);
                    } catch (ParseException e1) {
                        throw new StatusAndMessageError(-9,e);
                    }
                } else if (Date.class.isAssignableFrom(tClass)){
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return (T) sdf.parse(value);
                    } catch (ParseException e1) {
                        throw new StatusAndMessageError(-9,e);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getVariable(String key) {
        return getVariable(String.class,key);
    }

    @Override
    public Collection<SysVariable> allVariables() {
        return variableDao.findAll();
    }

    protected void setupStatusCls(Metamodel metamodel){
        List<Class<? extends StatusAndType>> result = new LinkedList<>();

        for (EntityType<?> entityType : metamodel.getEntities()){
            Class<?> cls = entityType.getJavaType();

            if (StatusAndType.class!=cls && StatusAndType.class.isAssignableFrom(cls)){
                DiscriminatorValue discriminatorValue = cls.getAnnotation(DiscriminatorValue.class);
                if (discriminatorValue!=null){
                    String typeName = discriminatorValue.value();
                    cache.put((Class<? extends StatusAndType>) cls,typeName);
                }
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        metamodel = emf.getMetamodel();

        setupStatusCls(metamodel);
    }
}
