package cn.net.xyan.blossom.platform.ui.view.entity;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.core.utils.StringUtils;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.support.MultiSelectConverter;
import cn.net.xyan.blossom.platform.ui.view.entity.filter.SingleAttributeSpecification;
import cn.net.xyan.blossom.platform.ui.view.entity.filter.UISpecification;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewServiceImpl;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.jodah.typetools.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tepi.listbuilder.ListBuilder;

import javax.annotation.Nonnull;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.*;
import java.util.*;

/**
 * Created by zarra on 16/6/4.
 */
public class EntityRenderConfiguration<E> {

    EntityManagerFactory emf ;
    Metamodel metamodel;
    Class<E> entityType;
    EntityType<E> entity;
    protected Logger logger;

    List<TableColumnHeaderConfig> tableColumnHeaderConfigs = new LinkedList<>();

    List<FormFieldConfig> formFieldConfigs = new LinkedList<>();

    List<UISpecification<E>> specifications = new LinkedList<>();

    public static class NullConverter implements Converter {

        @Override
        public Object convertToModel(Object value, Class targetType, Locale locale) throws ConversionException {
            if (value == null)
                return null;
            return value.toString();
        }

        @Override
        public Object convertToPresentation(Object value, Class targetType, Locale locale) throws ConversionException {
            if (value == null)
                return "";
            return value.toString();
        }

        @Override
        public Class getModelType() {
            return String.class;
        }

        @Override
        public Class getPresentationType() {
            return String.class;
        }
    }

    static public class TableColumnHeaderConfig{

        TableValueConverter<?> converter;
        String field;
        String displayTitle;

        public TableColumnHeaderConfig(String field){
            this.field = field;
            this.displayTitle = field;
        }

        public  TableValueConverter<?> getConverter() {
            return converter;
        }

        public TableColumnHeaderConfig setConverter( TableValueConverter<?> converter) {
            this.converter = converter;
            return this;
        }

        public String getDisplayTitle() {
            return displayTitle;
        }

        public TableColumnHeaderConfig setDisplayTitle(String displayTitle) {
            this.displayTitle = displayTitle;
            return this;
        }

        public String getField() {
            return field;
        }

        public TableColumnHeaderConfig setField(String field) {
            this.field = field;
            return this;
        }
    }

    static public class FormFieldConfig{

        public interface FormFieldSetup{
            void fieldSetup(AbstractField field,EntityEditFrom<?> parent,AbstractOrderedLayout formLayout,Map<String,AbstractField> fieldGroup);
        }

        public static String entityFormFieldCaptionKey(Class<?> entityType,String fieldName){
            return String.format("entity.%s.%s",entityType.getName(),fieldName);
        }

        String field;
        Class<? extends AbstractField> fieldType;

        Class<?> valueType;

        FormFieldSetup formFieldSetup;

        FormFieldSetup formFieldAfterBind;

        public FormFieldConfig(String field,Class<?> valueType ,Class<? extends AbstractField> fieldType){
            this.field = field;
            this.fieldType = fieldType;
            this.valueType = valueType;
            //this.displayTitle = field;
        }

        public String getField() {
            return field;
        }

        public FormFieldConfig setField(String field) {
            this.field = field;
            return this;
        }

        public Class<? extends AbstractField> getFieldType() {
            return fieldType;
        }

        public FormFieldConfig setFieldType(Class<? extends AbstractField> fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public FormFieldSetup getFormFieldSetup() {
            return formFieldSetup;
        }

        public FormFieldConfig setFormFieldSetup(FormFieldSetup formFieldSetup) {
            this.formFieldSetup = formFieldSetup;
            return this;
        }

        public FormFieldSetup getFormFieldAfterBind() {
            return formFieldAfterBind;
        }

        public FormFieldConfig setFormFieldAfterBind(FormFieldSetup formFieldAfterBind) {
            this.formFieldAfterBind = formFieldAfterBind;
            return this;
        }

        protected FormFieldConfig addFormFieldSetupInner(final FormFieldSetup newSetup, FormFieldSetup before,boolean after){
            final FormFieldSetup prev = before;
            FormFieldSetup setup = new FormFieldSetup() {
                @Override
                public void fieldSetup(AbstractField field, EntityEditFrom<?> parent, AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {
                    if (prev != null)
                        prev.fieldSetup(field,parent,formLayout,fieldGroup);

                    if (newSetup != null)
                        newSetup.fieldSetup(field,parent,formLayout,fieldGroup);
                }
            };

            if (after)
                setFormFieldAfterBind(setup);
            else{
               setFormFieldSetup(setup);
            }

            return this;
        }

        public FormFieldConfig addFormFieldSetup(final FormFieldSetup formFieldSetup){
            return addFormFieldSetupInner(formFieldSetup,getFormFieldSetup(),false);
        }

        public FormFieldConfig addFormFieldAfterBind(final FormFieldSetup formFieldSetup){
            return addFormFieldSetupInner(formFieldSetup,getFormFieldAfterBind(),true);
        }

        public Class<?> getValueType() {
            return valueType;
        }

        public FormFieldConfig setValueType(Class<?> valueType) {
            this.valueType = valueType;
            return this;
        }
    }

    public EntityRenderConfiguration(Class<E> eClass){
        this.entityType = eClass;
        logger = LoggerFactory.getLogger(this.getClass());
        emf = ApplicationContextUtils.getBean(EntityManagerFactory.class);
        metamodel = emf.getMetamodel();

        entity = metamodel.entity(getEntityCls());
    }

    public EntityRenderConfiguration(){
        this(null);
    }

    public void init(){
        configTableColumnHeader();
        configFormFiled();
        configSpecification();
    }

    public Class<E> getEntityCls() {
        if (entityType == null) {
            Class<? extends EntityRenderConfiguration> thisCls = this.getClass();

            Class<?>[] typeArgs = TypeResolver.resolveRawArguments(EntityRenderConfiguration.class, thisCls);
            if (typeArgs.length > 0) {
                entityType = (Class<E>) typeArgs[0];
            }
        }
        return entityType;
    }

    public EntityType<E> getEntityType() {
        return entity;
    }

    public List<TableColumnHeaderConfig> getTableColumnHeaderConfigs() {
        return tableColumnHeaderConfigs;
    }

    public List<FormFieldConfig> getFormFieldConfigs() {
        return formFieldConfigs;
    }

    public List<UISpecification<E>> getSpecifications() {
        return specifications;
    }

    public String getIdPropertyName(){
        Type<?> idType = entity.getIdType();
        return entity.getId(idType.getJavaType()).getName();
    }

    public String getEntityTypeTitle(){
        return String.format("entity.%s",getEntityCls().getName());
    }

    public String getEntityTitle(@Nonnull E entity){
        return entity.toString();
    }

    public TableColumnHeaderConfig addTableColumn(@Nonnull String field){
        TableColumnHeaderConfig tchc = new TableColumnHeaderConfig(field);
        tableColumnHeaderConfigs.add(tchc);
        return tchc;
    }

    public TableColumnHeaderConfig addTableColumn( Attribute<?, ?> ... attributes){
        return addTableColumn(Arrays.asList(attributes));
    }

    public TableColumnHeaderConfig addTableColumn( List<Attribute<? , ?>> attributes){
        List<String> stringList = new LinkedList<>();

        for (Attribute<? , ?> a :attributes){
            stringList.add(a.getName());
        }

        String path = StringUtils.join(stringList,".");

        return addTableColumn(path);
    }

    public TableColumnHeaderConfig addTableColumn(@Nonnull Attribute<? super E, ?> attribute){
        return addTableColumn(attribute.getName());
    }

    public FormFieldConfig addFormField(@Nonnull String field,@Nonnull Class<?> valueType,@Nonnull Class<? extends AbstractField> fieldType){
        FormFieldConfig formFieldConfig = new FormFieldConfig(field,valueType,fieldType);
        getFormFieldConfigs().add(formFieldConfig);
        return formFieldConfig;
    }

    public FormFieldConfig formFieldForAttribute(@Nonnull Attribute<? , ?> attribute,boolean ignoreCollection){
        String field = attribute.getName();
        Class<?> valueType = attribute.getJavaType();

        Class<? extends  AbstractField> fieldType = null;

        JPAContainer<?> jpaContainer = null;

        boolean isCollection = false;

        if (Number.class.isAssignableFrom(valueType)){
            fieldType = NumberField.class;
        }
        else if (Date.class.isAssignableFrom(valueType)){
            fieldType = DateField.class;
        }
        else if (EntityViewServiceImpl.isPrimaryType(valueType)){
            fieldType = TextField.class;
        }
        else if (attribute instanceof  PluralAttribute ){

            PluralAttribute<? super E,?,?> pluralAttribute = (PluralAttribute<? super E, ?, ?>) attribute;

            Class<?> componentType = pluralAttribute.getElementType().getJavaType();
            jpaContainer = EntityContainerFactory.jpaContainerReadOnly(componentType);

            if (ignoreCollection){
                fieldType = ComboBox.class;
            }else {
                fieldType = TwinColSelect.class;
                isCollection = true;
            }
        }else if (metamodel.entity(valueType)!= null){
            fieldType = ComboBox.class;
            jpaContainer = EntityContainerFactory.jpaContainerReadOnly(valueType);
        }

        final JPAContainer<?> jpaContainerInner = jpaContainer;
        final boolean isCollectionInner = isCollection;

        if (field!=null) {
            FormFieldConfig formFieldConfig = new FormFieldConfig(field, valueType, fieldType);

            if (TextField.class.isAssignableFrom(fieldType)){
                formFieldConfig.setFormFieldSetup(new FormFieldConfig.FormFieldSetup() {
                    @Override
                    public void fieldSetup(AbstractField field, EntityEditFrom<?> parent,AbstractOrderedLayout formLayout, Map<String, AbstractField> fieldGroup) {
                        field.setConverter(new NullConverter());
                        field.setWidth("100%");
                    }
                });
            }

            if (jpaContainer != null && AbstractSelect.class.isAssignableFrom(fieldType)){
                formFieldConfig.setFormFieldSetup(new FormFieldConfig.FormFieldSetup() {
                    @Override
                    public void fieldSetup(AbstractField field, EntityEditFrom<?> parent, AbstractOrderedLayout formLayout,Map<String, AbstractField> fieldGroup) {
                        AbstractSelect select = (AbstractSelect) field;
                        jpaContainerInner.refresh();
                        select.setContainerDataSource(jpaContainerInner);
                        select.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ITEM);
                        if (isCollectionInner){
                            select.setConverter(new MultiSelectConverter(select));
                            select.setSizeFull();
                            //select.setWidth("100%");
                            formLayout.setExpandRatio(select,1);
                        }else {
                            select.setConverter(new SingleSelectConverter(select));
                            select.setWidth("100%");
                        }
                    }
                });
            }
            return formFieldConfig;
        }
        return null;
    }

    public FormFieldConfig addFormField(@Nonnull Attribute<? super E, ?> attribute){
        FormFieldConfig formFieldConfig = formFieldForAttribute(attribute,false);
        if (formFieldConfig != null)
            getFormFieldConfigs().add(formFieldConfig);
        return formFieldConfig;
    }

    public UISpecification<E> addSpecification(UISpecification<E> specification){
        if (specification!=null)
            getSpecifications().add(specification);
        return specification;
    }

    public UISpecification<E> addSpecification(JPA.Operator operator,Attribute<?,?> ... attributes){
        List<Attribute<?,?>> attributeList = Arrays.asList(attributes);
        Attribute<?,?> last = null ;
        if (attributeList.size()>0){
            last = attributeList.get(attributeList.size()-1);
        }else{
            throw new StatusAndMessageError(-9,"attributes length must > 0");
        }
        Class<?> valueType = SingleAttributeSpecification.valueTypeForAttribute(last);
        UISpecification<E> uiSpecification =  new SingleAttributeSpecification<>(getEntityCls(),valueType,attributeList,operator);

        addSpecification(uiSpecification);

        return uiSpecification;

    }

    public Component tableDisplayComponent(@Nonnull E entity){
        Button button = new Button(getEntityTitle(entity));
        button.addStyleName(ValoTheme.BUTTON_LINK);
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addStyleName("in-table");
        //button.setPrimaryStyleName("in-table");
        return button;
    }

    public void configTableColumnHeader(){

        String idName = getIdPropertyName();

        addTableColumn(idName);

        for (Attribute<? super E, ?> attribute : entity.getAttributes()){
            String name = attribute.getName();
            if (!name.equals(idName))
                addTableColumn(name);
        }

    }

    public void configFormFiled(){
        String idName = getIdPropertyName();

        Attribute<? super E, ?> attributeId = entity.getAttribute(idName);

        addFormField(attributeId);

        for (Attribute<? super E, ?> attribute : entity.getAttributes()){
            String name = attribute.getName();
            if (!name.equals(idName))
                addFormField(attribute);
        }
    }

    public void configSpecification(){

    }
}
