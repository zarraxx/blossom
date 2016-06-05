package cn.net.xyan.blossom.platform.ui.view.entity.service;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityColumnGenerator;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityEditFrom;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.Renderer;
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

    public <E> String addGenerateColumn(Table table, JPAContainer<E> jpaContainer,String path){
        String newFieldName = "__gen__"+path;

        table.addGeneratedColumn(newFieldName, new EntityColumnGenerator<>(this,jpaContainer,path));


        return newFieldName;
    }

    @Override
    public void addScanPackage(String packageName) {
        scanPackages.add(packageName);
    }

    @Override
    public <E> EntityRenderConfiguration<E> entityRenderConfiguration(Class<E> eClass) {
        return (EntityRenderConfiguration<E>) entityConfigurationCache.get(eClass);
    }

    @Override
    public <E> EntityEditFrom<E> createEntityForm(@Nonnull E entity, @Nonnull EntityEditFrom.FormStatus status) {
        Class<E> eClass = (Class<E>) entity.getClass();
        EntityRenderConfiguration<E> renderConfiguration = (EntityRenderConfiguration<E>) entityConfigurationCache.get(eClass);
        EntityEditFrom<E> from = new EntityEditFrom<>(entity,renderConfiguration,status);
        return from;
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

            Attribute<? super E, ?> attribute =  entityType.getAttribute(field);

            if (attribute!=null && isPrimaryType(attribute.getJavaType())){
                //doNothing
            }else {
                field = addGenerateColumn(table,jpaContainer,field);


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
