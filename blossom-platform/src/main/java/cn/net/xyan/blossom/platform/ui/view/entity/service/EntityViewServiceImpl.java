package cn.net.xyan.blossom.platform.ui.view.entity.service;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.ui.view.entity.*;
import cn.net.xyan.blossom.platform.ui.view.entity.filter.EntityFilterForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Table;
import net.jodah.typetools.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.*;

/**
 * Created by zarra on 16/6/4.
 */
public class EntityViewServiceImpl  extends InstallerAdaptor implements EntityViewService {

    Logger logger = LoggerFactory.getLogger(EntityViewServiceImpl.class);

    List<String> scanPackages = new LinkedList<>();

    Map<Class<?> ,EntityRenderConfiguration<?>> entityConfigurationCache = new HashMap<>();

    public EntityViewServiceImpl(){
        logger.info("EntityViewServiceImpl");
    }

    private <E> EntityRenderConfiguration<E> entityRenderConfiguration(EntityView<E> entityView){
        return (EntityRenderConfiguration<E>) entityConfigurationCache.get(entityView.getEntityCls());
    }

    static public boolean isPrimaryType(Class<?> javaType){
        if (javaType.isPrimitive()){
            return true;
        }
        if (Number.class.isAssignableFrom(javaType)){
            return true;
        }
        else if (String.class.isAssignableFrom(javaType)){
            return true;
        }
        else if (Date.class.isAssignableFrom(javaType)){
            return true;
        }

//        else if (javaType.isArray()){
//            Class ofArray = javaType.getComponentType();
//            return isPrimaryType(ofArray);
//        }


        return false;
    }

    static public boolean isCollectionType(Class<?> javaType){
        return Collection.class.isAssignableFrom(javaType);
    }

    static public Class<?> componentType(Class<? extends Collection> collectionType){
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(Collection.class, collectionType);
        if (typeArgs.length>0)
            return typeArgs[0];
        return null;
    }

    public <E> String addGenerateColumn(Table table, JPAContainer<E> jpaContainer,String path,TableValueConverter<?> converter){
        String newFieldName = "__gen__"+path;

        table.addGeneratedColumn(newFieldName, new EntityColumnGenerator<>(this,jpaContainer,path,converter));


        return newFieldName;
    }

    @Override
    public void addScanPackage(String packageName) {
        scanPackages.add(packageName);
    }

    @Override
    public <E> EntityRenderConfiguration<? super E> entityRenderConfiguration(Class<E> eClass) {
        try {
            if (EntityUtils.metamodel().entity(eClass) == null) return null;
        }catch (Throwable e){
            return null;
        }

         EntityRenderConfiguration<? super E> result = (EntityRenderConfiguration<? super E>) entityConfigurationCache.get(eClass);

        if (result != null)
            return result;
        else
            return entityRenderConfiguration((Class<? super E>) eClass.getSuperclass());
    }

    @Override
    public <E> EntityEditForm<? super E> createEntityForm(@Nonnull E entity, @Nonnull EntityEditForm.FormStatus status) {
        Class< E> eClass = (Class<E>) entity.getClass();
        EntityRenderConfiguration<? super E> renderConfiguration = entityRenderConfiguration( eClass);
        EntityEditForm<? super E> from = new EntityEditForm<>(entity,renderConfiguration,status);
        return from;
    }

    @Override
    public <E> EntityEditForm<? super E> createEntityForm(@Nonnull EntityItem<E> entityItem, @Nonnull EntityEditForm.FormStatus status) {
        Class<E> eClass = (Class<E>) entityItem.getEntity().getClass();
       return createEntityForm(eClass,entityItem,status);
    }

    @Override
    public <E> EntityEditForm<? super E> createEntityForm(@Nonnull Class<E> eClass, @Nonnull EntityItem<E> entityItem, @Nonnull EntityEditForm.FormStatus status) {
        EntityRenderConfiguration<? super E> renderConfiguration = entityRenderConfiguration(eClass);
        if (renderConfiguration == null)
            throw new NullPointerException();
        EntityEditForm<? super E> from = new EntityEditForm<E>(entityItem,renderConfiguration,status);
        return from;
    }

    @Override
    public <E> EntityFilterForm<E> createEntityFilter(@Nonnull EntityView<E> entityView) {
        EntityRenderConfiguration<E> entityRenderConfiguration = entityRenderConfiguration(entityView);
        EntityFilterForm<E> filterForm = new EntityFilterForm<>(entityRenderConfiguration,entityView.getContainer());
        return filterForm;
    }

    @Override
    public <E> void setupEntityViewTable(EntityView<E> entityView) {

        EntityRenderConfiguration<E> entityRenderConfiguration = entityRenderConfiguration(entityView);

        if (entityRenderConfiguration == null)
            return;

        Table table = entityView.getTable();

        JPAContainer<E> jpaContainer = entityView.getContainer();

        List<EntityRenderConfiguration.TableColumnHeaderConfig> tableColumnHeaderConfigs
                = entityRenderConfiguration.getTableColumnHeaderConfigs();

        List<String> visibleColumns = new LinkedList<>();

        EntityType<E> entityType = entityRenderConfiguration.getEntityType();

        for(EntityRenderConfiguration.TableColumnHeaderConfig columnHeaderConfig: tableColumnHeaderConfigs){
            String field = columnHeaderConfig.getField();
            String displayName = columnHeaderConfig.getDisplayTitle();

            Attribute<? super E, ?> attribute =  null ;
            try {
                attribute = entityType.getAttribute(field);
            }catch (IllegalArgumentException e){

            }

            TableValueConverter<?> converter = columnHeaderConfig.getConverter();

            if (attribute!=null && isPrimaryType(attribute.getJavaType())){

                if (converter!=null){
                    table.setConverter(field,converter);
                }

            }else {
                field = addGenerateColumn(table,jpaContainer,field,converter);
            }
            visibleColumns.add(field);
            table.setColumnHeader(field,displayName);
        }


        table.setVisibleColumns(visibleColumns.toArray(new String[0]));


    }

    @Override
    public void afterSetup() {

        Set< Class<? extends EntityRenderConfiguration>> classSet =
                ReflectUtils.scanPackages(EntityRenderConfiguration.class,scanPackages.toArray(new String[0]));


        for (Class<? extends EntityRenderConfiguration> configurationCls : classSet){
            EntityRenderConfiguration<?> configuration = null;
            try {
                configuration = configurationCls.newInstance();

                configuration.init();

                Class<?> eClass = configuration.getEntityCls();
                entityConfigurationCache.put(eClass,configuration);
            } catch (Throwable e) {
                ExceptionUtils.traceError(e,logger);
            }

        }
    }

    @Override
    public void beforeSetup() {
        addScanPackage("cn.net.xyan.blossom");
    }
}
