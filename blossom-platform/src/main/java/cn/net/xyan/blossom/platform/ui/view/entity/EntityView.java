package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.jodah.typetools.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zarra on 16/6/2.
 */
public class EntityView<E>  extends VerticalLayout implements View {

    public static class EntityFormWindow extends Window{
        EntityEditFrom<?> entityForm;

        public EntityFormWindow(EntityEditFrom<?> entityForm){
            this.entityForm = entityForm;

            //setSizeUndefined();
            setWidth("400px");
            setHeight("600px");
            setContent(entityForm);
            setCaption("AAA");
        }

        public static EntityFormWindow displayEntityForm(EntityEditFrom<?> entityForm, EntityEditFrom.EntityFormListener listener){
            EntityFormWindow window = new EntityFormWindow(entityForm);
            if (listener!=null)
                entityForm.addEntityFormListener(listener);

            entityForm.addEntityFormListener(new EntityEditFrom.EntityFormListener() {
                @Override
                public void onEvent(EntityEditFrom.EntityFormEvent event) {
                    if (EntityEditFrom.EntityFormEvent.Type.NeedClose == event.getType()){
                        window.close();
                    }
                }
            });

            if (EntityEditFrom.FormStatus.Add == entityForm.getStatus()){
                window.setCaption(TR.m(TR.Add,"New"));
            }
            else if (EntityEditFrom.FormStatus.Edit == entityForm.getStatus()){
                window.setCaption(TR.m(TR.Edit,"Edit"));
            }
            else if (EntityEditFrom.FormStatus.ReadOnly == entityForm.getStatus()){
                window.setCaption(TR.m(TR.View,"View"));
            }

            UI.getCurrent().addWindow(window);

            return window;
        }
    }

    Class<E> entityCls;

    String title;

    protected Logger logger;

    protected Table table;

    protected JPAContainer<E> container;

    protected HorizontalLayout buttonLayout;

    protected Button bAdd;

    protected Button bEdit;

    protected Button bRemove;

    EntityViewService entityViewService;

    public EntityView(String title){
        logger = LoggerFactory.getLogger(this.getClass());
        setTitle(title);
        initView();
    }

    public EntityView(){
        this("Abstract Entity View");
    }

    public void initView(){

        setSpacing(true);
        setMargin(true);
        setSizeFull();

        Label header = new Label(getTitle());
        header.addStyleName(ValoTheme.LABEL_H1);

        addComponent(header);

        Label hr = new Label("<hr/>", ContentMode.HTML);

        addComponent(hr);

        addComponent(initButtonLayout());

        Table table = createTable();

        addComponent(table);

        setExpandRatio(table,1);

    }

    public HorizontalLayout initButtonLayout(){
        buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        bAdd = new Button(TR.m(TR.Add,"Add"));

        bEdit = new Button(TR.m(TR.Edit,"Edit"));

        bRemove = new Button(TR.m(TR.Delete,"Delete"));

        buttonLayout.addComponent(bAdd);

        buttonLayout.addComponent(bEdit);

        buttonLayout.addComponent(bRemove);

        bAdd.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickAdd();
            }
        });

        return buttonLayout;
    }

    public void showEntityForm(E entity, EntityEditFrom.FormStatus status){
        EntityEditFrom<E> form = entityViewService.createEntityForm(entity,status);

        EntityFormWindow formWindow = EntityFormWindow.displayEntityForm(form, new EntityEditFrom.EntityFormListener() {
            @Override
            public void onEvent(EntityEditFrom.EntityFormEvent event) {
                if (!event.isHasCommit()){

                }else{
                    EntityItem<E> bi = event.getBeanItem();

                    getContainer().addEntity(bi.getEntity());

                }
            }
        });
    }
    public void onClickAdd(){
        try {
            E entity = getEntityCls().newInstance();
            showEntityForm(entity, EntityEditFrom.FormStatus.Add);
        }catch (Throwable e){
            throw  new StatusAndMessageError(-9,e);
        }
    }

    public Class<E> getEntityCls() {
        if (entityCls == null) {
            Class<? extends EntityView> thisCls = this.getClass();

            Class<?>[] typeArgs = TypeResolver.resolveRawArguments(EntityView.class, thisCls);
            if (typeArgs.length > 0) {
                entityCls = (Class<E>) typeArgs[0];
                logger.trace("entityCls:" + entityCls.getName());
            }
        }
        return entityCls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JPAContainer<E> getContainer() {
        return container;
    }

    public void setContainer(JPAContainer<E> container) {
        this.container = container;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Table createTable(){
        table = new Table();
        table.setSizeFull();
        return table;
    }

    public JPAContainer<E> createContainer(){
        container = EntityContainerFactory.jpaContainer(getEntityCls());
        return container;
    }

    @Override
    public void attach() {
        super.attach();
        if (entityViewService == null)
            entityViewService = ApplicationContextUtils.getBean(EntityViewService.class);
        if (container == null) {
            JPAContainer<E> container = createContainer();
            table.setContainerDataSource(container);

            entityViewService.setupEntityViewTable(this);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
