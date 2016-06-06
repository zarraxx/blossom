package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.platform.dao.StatusDao;
import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import cn.net.xyan.blossom.platform.service.DictService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zarra on 16/6/6.
 */
public class DictServiceImpl implements DictService,InitializingBean {

    @PersistenceUnit
    EntityManagerFactory emf;

    @Autowired
    StatusDao statusDao;

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
