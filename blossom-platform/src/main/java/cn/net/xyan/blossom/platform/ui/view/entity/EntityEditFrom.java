package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.I18NService;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zarra on 16/6/4.
 */
public class EntityEditFrom<E> extends VerticalLayout implements Button.ClickListener {

    public static class EntityFormEvent {
        public enum Type {
            NeedClose
        }

        Type type;

        boolean hasCommit;

        EntityItem beanItem;

        public EntityFormEvent(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public boolean isHasCommit() {
            return hasCommit;
        }

        public void setHasCommit(boolean hasCommit) {
            this.hasCommit = hasCommit;
        }

        public EntityItem getBeanItem() {
            return beanItem;
        }

        public void setBeanItem(EntityItem beanItem) {
            this.beanItem = beanItem;
        }
    }

    public interface EntityFormListener {
        void onEvent(EntityFormEvent event);
    }

    E entity;
    JPAContainer<E> jpaContainer;
    EntityItem<E> beanItem;
    EntityRenderConfiguration<E> renderConfiguration;

    FormStatus status;

    FieldGroup fieldGroup;

    I18NService i18NService;

    Button bOk;
    Button bCancel;

    List<EntityFormListener> listeners = new LinkedList<>();

    public enum FormStatus{
        Add,Edit,ReadOnly
    }

    public EntityEditFrom(EntityItem<E> item,EntityRenderConfiguration<E> renderConfiguration,FormStatus status){
        this.beanItem = item;
        this.jpaContainer = (JPAContainer<E>) item.getContainer();
        this.entity = item.getEntity();

        init(renderConfiguration,status);
    }
    public EntityEditFrom(E entity,EntityRenderConfiguration<E> renderConfiguration,FormStatus status){

        Class<E> eClass = (Class<E>) entity.getClass();
        this.entity = entity;
        jpaContainer = EntityContainerFactory.jpaContainer(eClass);
        this.beanItem = jpaContainer.createEntityItem(entity);

        init(renderConfiguration,status);

    }

    public void init(EntityRenderConfiguration<E> renderConfiguration,FormStatus status){

        this.renderConfiguration = renderConfiguration;
        this.status = status;

        i18NService = ApplicationContextUtils.getBean(I18NService.class);

        fieldGroup = new FieldGroup(beanItem);

        setSpacing(true);
        setMargin(true);

        setSizeFull();

        String title = EntityRenderConfiguration.getEntityTypeTitle(entity.getClass());

        I18NString string = i18NService.setupMessage(title,entity.getClass().getSimpleName());
        String prefix = null;

        if (FormStatus.Add.equals(status)){
            prefix = TR.m(TR.Add,"Add");
        }else if(FormStatus.Edit.equals(status)){
            prefix = TR.m(TR.Edit,"Edit");
        }else{
            prefix = TR.m(TR.View,"View");
        }

        Label header = new Label(prefix+" "+string.value());


        AbstractOrderedLayout formLayout = createForm(fieldGroup, renderConfiguration, status);

        HorizontalLayout buttonLayout = createbuttonLayout(status);


        addComponent(header);

        addComponent(formLayout);

        setExpandRatio(formLayout,1);

        addComponent(buttonLayout);


    }

    public void setFieldCaption(AbstractField field,EntityRenderConfiguration.FormFieldConfig formFieldConfig){
        String propertyName = formFieldConfig.getField();
        String key = EntityRenderConfiguration.FormFieldConfig.entityFormFieldCaptionKey(entity.getClass(),propertyName);
        I18NString string = i18NService.setupMessage(key,propertyName);
        field.setCaption(string.value());
    }

    public AbstractOrderedLayout createForm(FieldGroup fieldGroup, EntityRenderConfiguration<E> renderConfiguration, FormStatus mode){
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSizeFull();
        formLayout.setSpacing(true);

        Map<String,AbstractField> cache = new HashMap<>();
        for (EntityRenderConfiguration.FormFieldConfig config : renderConfiguration.getFormFieldConfigs()){

            try {
                String propertyName = config.getField();

                Class<? extends AbstractField> fieldType = config.getFieldType();

                AbstractField viewer = fieldType.newInstance();

                setFieldCaption(viewer,config);

                cache.put(propertyName,viewer);

                formLayout.addComponent(viewer);

            }catch (Throwable e){

            }
        }

        for (EntityRenderConfiguration.FormFieldConfig config : renderConfiguration.getFormFieldConfigs()){
            EntityRenderConfiguration.FormFieldConfig.FormFieldSetup setup = config.getFormFieldSetup();
            AbstractField field = cache.get(config.getField());
            if (setup!=null){
                setup.fieldSetup(field,this,formLayout,cache);
            }

            fieldGroup.bind(field, config.getField());

            setup = config.getFormFieldAfterBind();

            if (setup!=null){
                setup.fieldSetup(field,this,formLayout,cache);
            }
        }

        if (this.status == FormStatus.Edit){
            fieldGroup.getField(renderConfiguration.getIdPropertyName()).setReadOnly(true);
        }

        return formLayout;
    }

    public HorizontalLayout createbuttonLayout(FormStatus mode) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        bOk = new Button(TR.m(TR.OK,"OK"), FontAwesome.SAVE);
        bOk.addClickListener(this);

        bCancel = new Button(TR.m(TR.Cancel,"Cancel"), FontAwesome.REMOVE);
        bCancel.addClickListener(this);

        if (FormStatus.ReadOnly == mode){
            bCancel.setCaption(TR.m(TR.Close,"Close"));
            bCancel.setIcon(FontAwesome.CLOSE);
            horizontalLayout.addComponent(bCancel);
        }else {
            horizontalLayout.addComponent(bOk);
            horizontalLayout.addComponent(bCancel);
        }

        return horizontalLayout;
    }

    public void fireEntityFormEvent(EntityFormEvent event) {
        for (EntityFormListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void addEntityFormListener(EntityFormListener listener) {
        listeners.add(listener);
    }

    public void removeEntityFormListener(EntityFormListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        EntityFormEvent e = new EntityFormEvent(EntityFormEvent.Type.NeedClose);
        e.setBeanItem(this.beanItem);

        if (event.getButton() == bOk) {
            try {
                fieldGroup.commit();
                Notification.show("ok");
                e.setHasCommit(true);
                fireEntityFormEvent(e);
            } catch (FieldGroup.CommitException e1) {
                e1.printStackTrace();
                Notification.show("fail");
            }
        } else {
            fieldGroup.discard();
            e.setHasCommit(false);
            fireEntityFormEvent(e);
        }
    }

    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }
}
