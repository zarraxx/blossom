package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.ui.view.entity.filter.EntityFilterForm;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.sass.internal.tree.controldirective.ElseNode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.jodah.typetools.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.jpa.domain.Specifications;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.hene.popupbutton.PopupButton;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderPanelListener;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by zarra on 16/6/2.
 */
public class EntityView<E>  extends VerticalLayout implements View,InitializingBean {

    //@Autowired
    I18NService i18NService;

    @Override
    public void afterPropertiesSet() throws Exception {
        String titleKey = String.format("ui.view.entity.%s.title.head",getClass().getName());
         I18NString i18NString = i18NService.setupMessage(titleKey,getTitle());

        if (header!=null)
            header.setValue(i18NString.value());
    }

    public static class EntityFormWindow extends Window{
        EntityEditForm<?> entityForm;

        public EntityFormWindow(EntityEditForm<?> entityForm){
            this.entityForm = entityForm;

            //setSizeUndefined();
            setWidth("400px");
            setHeight("600px");

            Panel panel = new Panel(entityForm);
            panel.setSizeFull();

            setContent(panel);
            setCaption("AAA");
        }

        public static EntityFormWindow displayEntityForm(EntityEditForm<?> entityForm, EntityEditForm.EntityFormListener listener){
            EntityFormWindow window = new EntityFormWindow(entityForm);
            if (listener!=null)
                entityForm.addEntityFormListener(listener);

            entityForm.addEntityFormListener(new EntityEditForm.EntityFormListener() {
                @Override
                public void onEvent(EntityEditForm.EntityFormEvent event) {
                    if (EntityEditForm.EntityFormEvent.Type.NeedClose == event.getType()){
                        window.close();
                    }
                }
            });

            if (EntityEditForm.FormStatus.Add == entityForm.getStatus()){
                window.setCaption(TR.m(TR.Add,"New"));
            }
            else if (EntityEditForm.FormStatus.Edit == entityForm.getStatus()){
                window.setCaption(TR.m(TR.Edit,"Edit"));
            }
            else if (EntityEditForm.FormStatus.ReadOnly == entityForm.getStatus()){
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

    protected SliderPanel pFilter;

    protected Label header;

    EntityViewService entityViewService;

    public EntityView(String title){
        logger = LoggerFactory.getLogger(this.getClass());
        setTitle(title);
        i18NService = ApplicationContextUtils.getBean(I18NService.class);
        initView();
    }

    public EntityView(){
        this("Abstract Entity View");
    }

    public void initView(){

        setSpacing(true);
        //setMargin(true);

        setMargin(new MarginInfo(false,true,true,true));
        setSizeFull();

        header = new Label(getTitle());
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
        Class<? extends E> eClass = getEntityCls();

        if (Modifier.isAbstract(eClass.getModifiers())){
            // add a popup button for abstract entity

            List<EntityType<? extends E>> childTypes = EntityUtils.allEntityTypeInheritClass((Class<E>) eClass);

            if (childTypes.size()>0) {

                PopupButton popupButton = new PopupButton(TR.m(TR.Add, "Add"));

                VerticalLayout popupLayout = new VerticalLayout();

                for(EntityType<? extends E> childType : childTypes){
                    Class<? extends E> childCls = childType.getJavaType();
                    I18NString childTypeTitle = i18NService.setupMessage(EntityRenderConfiguration.getEntityTypeTitle(childCls),childCls.getSimpleName());

                    Button button = new Button(childTypeTitle.value());

                    button.setStyleName(ValoTheme.BUTTON_LINK);

                    button.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            popupButton.setPopupVisible(false);
                            onClickAdd(childCls);
                        }
                    });

                    popupLayout.addComponent(button);
                    popupLayout.setComponentAlignment(button,Alignment.TOP_LEFT);

                }

                popupButton.setContent(popupLayout);


                bAdd = popupButton;
            }

        }else {
            bAdd = new Button(TR.m(TR.Add, "Add"));

            bAdd.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    onClickAdd(getEntityCls());
                }
            });
        }




        //------------------------------------------------

        bEdit = new Button(TR.m(TR.Edit,"Edit"));

        bRemove = new Button(TR.m(TR.Delete,"Delete"));

        buttonLayout.addComponent(bAdd);

        buttonLayout.addComponent(bEdit);

        buttonLayout.addComponent(bRemove);

        bEdit.setEnabled(false);

        bRemove.setEnabled(false);


        bEdit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickEdit();
            }
        });

        bRemove.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickRemove();
            }
        });



        return buttonLayout;
    }

    public void showEntityForm(EntityItem<? extends E> item, EntityEditForm.FormStatus status){
        //EntityEditFrom<E> form = entityViewService.createEntityForm(getEntityCls(),item,status);
        E entity = item.getEntity();
        EntityEditForm<? extends E> form;
        if (entity!=null) {
            Class<? extends  E> cls = (Class<? extends E>) entity.getClass();
            form = (EntityEditForm<? extends E>) entityViewService.<E>createEntityForm((Class<E>)cls,(EntityItem<E>) item, status);
        }else{
            form = (EntityEditForm<? extends E>) entityViewService.<E>createEntityForm(getEntityCls(),(EntityItem<E>)item,status);
        }

        EntityFormWindow formWindow = EntityFormWindow.displayEntityForm(form, new EntityEditForm.EntityFormListener() {
            @Override
            public void onEvent(EntityEditForm.EntityFormEvent event) {
                if (!event.isHasCommit()){

                }else{
                    EntityItem<E> bi = event.getBeanItem();

                    saveEntity(bi);

                }
            }
        });
    }

    public void saveEntity(EntityItem<E> bi){
        getContainer().addEntity(bi.getEntity());
    }

    public void onClickAdd(Class<? extends E> eClass){
        try {
            E entity = eClass.newInstance();
            JPAContainer<E> container;

            if (eClass.equals(getEntityCls()))
                container = getContainer();
            else
                container = EntityContainerFactory.jpaContainer((Class<E>) entity.getClass());

            EntityItem<? extends E> item = container.createEntityItem(entity);
            showEntityForm(item, EntityEditForm.FormStatus.Add);
        }catch (Throwable e){
            throw  new StatusAndMessageError(-9,e);
        }
    }

    public void onClickEdit(){
        Object itemId =  table.getValue();

        EntityItem<E> item = getContainer().getItem(itemId);

        E entity = item.getEntity();

        Class<? extends E> eClass = (Class<? extends E>) entity.getClass();

        JPAContainer<E> container;

        if (eClass.equals(getEntityCls())){
            container = getContainer();
        }else{
            container = EntityContainerFactory.jpaContainer((Class<E>)entity.getClass());
        }

        item = container.getItem(itemId);

        showEntityForm(item, EntityEditForm.FormStatus.Edit);
    }

    public void onClickRemove(){
        ConfirmDialog.show(UI.getCurrent(),
                TR.m(TR.Confirm,"Confirm"),
                TR.m("ui.view.entity.confirm.remove", "Are you sure to Delete?"),
                TR.m(TR.OK,"OK"),
                TR.m(TR.Cancel,"Cancel"),
                new ConfirmDialog.Listener() {
            @Override
            public void onClose(ConfirmDialog confirmDialog) {
                if (confirmDialog.isConfirmed()){
                    container.removeItem(table.getValue());
                    setModificationsEnabled(false);
                }
            }
        });

    }

    public void onClickFilter(){
        EntityFilterForm<E> filterForm = entityViewService.createEntityFilter(this);
        Window window = new Window();
        window.setContent(filterForm);
        UI.getCurrent().addWindow(window);
    }

    public void setModificationsEnabled(boolean b){
        bEdit.setEnabled(b);
        bRemove.setEnabled(b);
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

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                EntityView.this.setModificationsEnabled(event.getProperty().getValue() != null);
            }
        });
        return table;
    }

    public JPAContainer<E> createContainer(){
        container = EntityContainerFactory.jpaContainer(getEntityCls());
        return container;
    }



    public void onFilterClose(EntityFilterForm<E> filterForm){
        if (filterForm.inputOk()){
            Specifications<E> specifications = filterForm.createSpecifications();
            container.removeAllContainerFilters();
            container.setQueryModifierDelegate(new DefaultQueryModifierDelegate(){
                @Override
                public void filtersWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, List<Predicate> predicates) {
                    Root<E> root = (Root<E>) query.getRoots().iterator().next();
                    Predicate predicate = specifications.toPredicate(root,query,criteriaBuilder);
                    predicates.add(predicate);
                }
            });

            if (filterForm.isActive()){
                pFilter.setStyleName(SliderPanelStyles.COLOR_GREEN);
            }else {
                pFilter.setStyleName(SliderPanelStyles.COLOR_GRAY);
            }
            container.refresh();
        }else {
            Notification.show(TR.m("entity.filter.input.error.msg"),Notification.Type.ERROR_MESSAGE);
            pFilter.setExpanded(true, false);

        }
    }

    public void beforeAttach(){

    }


    @Override
    public void attach() {

        super.attach();

        if (entityViewService == null)
            entityViewService = ApplicationContextUtils.getBean(EntityViewService.class);
        if (container == null) {
            JPAContainer<E> container = createContainer();

            beforeAttach();

            table.setContainerDataSource(container);

            entityViewService.setupEntityViewTable(this);

            EntityRenderConfiguration<? super E> renderConfiguration = entityViewService.<E>entityRenderConfiguration(getEntityCls());

            if (renderConfiguration!=null && renderConfiguration.getSpecificationsConfig().size()>0){
                EntityFilterForm<E> filterForm = entityViewService.createEntityFilter(this);
                pFilter = new SliderPanelBuilder(filterForm)
                        .expanded(false)
                        .style(SliderPanelStyles.COLOR_GRAY)
                        .mode(SliderMode.TOP)
                        .caption(TR.m(TR.Filter,"Filter"))
                        .tabPosition(SliderTabPosition.END)
                        .build();
                pFilter.addListener(new SliderPanelListener() {
                    @Override
                    public void onToggle(boolean b) {
                        logger.info("toggle "+b);
                        onFilterClose(filterForm);

                            //throw new StatusAndMessageError(-1,"ad");

                    }
                });
                addComponentAsFirst(pFilter);
            }
        }




    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
