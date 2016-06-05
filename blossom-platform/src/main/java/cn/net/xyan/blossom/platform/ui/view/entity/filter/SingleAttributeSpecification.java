package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.*;
import net.jodah.typetools.TypeResolver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;

import java.util.*;

/**
 * Created by zarra on 16/6/5.
 */
public class SingleAttributeSpecification<E, V> extends UISpecification<E> {

    EntityManagerFactory emf;
    EntityType<E> rootType;
    Metamodel metamodel;

    V firstValue;

    V secondValue;

    Class<E> eClass;

    List<Attribute<?, ?>> attributes;

    HorizontalLayout uiContent;

    CheckBox checkbox;

    ComboBox comboBox;

    I18NService i18NService;

    EntityViewService entityViewService;

    Property<JPA.Operator> pOperator;

    Property<Boolean> pActive;

    Property<V> pFirst;

    Property<V> pSecond;

    AbstractField<V> fieldFirst;

    AbstractField<V> fieldSecond;

    JPA.Operator operator = JPA.Operator.Equal;

    final static List<JPA.Operator> availableOperator =
            Arrays.asList(
                    JPA.Operator.Equal,JPA.Operator.NotEqual,
                    JPA.Operator.IsNull,JPA.Operator.NotNull,
                    JPA.Operator.Greater,JPA.Operator.GreaterOrEqual,
                    JPA.Operator.Less,JPA.Operator.LessOrEqual,
                    JPA.Operator.Between,
                    JPA.Operator.Like);

    Map<JPA.Operator,I18NString> operatorStrings = new HashMap<>();

    public SingleAttributeSpecification(Class<E> eCls,Class<V> vClass, List<Attribute<?, ?>> attributes,JPA.Operator operator) {
        this.eClass = eCls;
        this.operator = operator;

        init(attributes);

    }

    public SingleAttributeSpecification(List<Attribute<?, ?>> attributes,JPA.Operator operator) {
        this.operator = operator;
        init(attributes);

    }

    public SingleAttributeSpecification(Attribute<E, ?> attribute,JPA.Operator operator) {
        this.operator = operator;
        init(Arrays.asList(attribute));
    }

    public static Attribute<?, ?> createAttributesFromStringList(String attributeName, EntityType<?> entityType) {
        return entityType.getAttribute(attributeName);
    }

    public static Class<?> valueTypeForAttribute(Attribute<?, ?> attribute) {
        Class<?> valueType = attribute.getJavaType();

        if (attribute instanceof PluralAttribute) {
            PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) attribute;

            valueType = pluralAttribute.getElementType().getJavaType();
        }

        return valueType;
    }

    public Attribute<?,?> lastAttribute(){
        List<Attribute<?,?>> attributes = getAttributes();
        return attributes.get(attributes.size()-1);
    }

    public Class<?> valueType(){
        return valueTypeForAttribute(lastAttribute());
    }

    public List<Attribute<?, ?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute<?, ?>> attributes) {
        this.attributes = attributes;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public void setMetamodel(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    public EntityType<E> getRootType() {
        return rootType;
    }

    public void setRootType(EntityType<E> rootType) {
        this.rootType = rootType;
    }

    public HorizontalLayout getUiContent() {
        return uiContent;
    }

    public void setUiContent(HorizontalLayout uiContent) {
        this.uiContent = uiContent;
    }

    public I18NString fieldName() {
        List<Attribute<?, ?>> attributes = getAttributes();
        if (attributes.size() > 0) {
            Attribute<?, ?> attribute = lastAttribute();
            ManagedType<?> declareType = attribute.getDeclaringType();

            String key = EntityRenderConfiguration.FormFieldConfig.entityFormFieldCaptionKey(declareType.getJavaType(), attribute.getName());

            return i18NService.setupMessage(key, attribute.getName());
        } else
            return null;
    }

    public List<Attribute<?, ?>> createAttributesFromStringList(List<String> stringList) {
        List<Attribute<?, ?>> attributes = new LinkedList<>();

        EntityType<?> entityType = rootType;

        for (String attributesName : stringList) {
            try {
                Attribute<?, ?> attribute = entityType.getAttribute(attributesName);

                attributes.add(attribute);

                Class<?> valueType = valueTypeForAttribute(attribute);

                entityType = metamodel.entity(valueType);
            } catch (Throwable e) {
                throw new StatusAndMessageError(-9, e);
            }
        }

        return attributes;
    }

    public Path<?> pathForAttributes(Root<E> root) {
        return pathForAttributes(getAttributes(), root);
    }

    public Path<?> pathForAttributes(List<Attribute<?, ?>> attributes, From<?, ?> from) {
        From<?, ?> innerForm = from;
        if (attributes.size() > 0) {
            Attribute<?, ?> attribute = attributes.get(0);
            if (attributes.size() == 1) {
                return innerForm.get(attribute.getName());
            } else {
                innerForm = innerForm.join(attribute.getName(), JoinType.LEFT);
                List<Attribute<?, ?>> newAttributes = attributes.subList(1, attributes.size());
                return pathForAttributes(newAttributes, innerForm);
            }
        } else {
            return null;
        }
    }


    protected void setupOperatorName(){
        operatorStrings.put(JPA.Operator.Equal,i18NService.setupMessage("filter.equal","Equal"));
        operatorStrings.put(JPA.Operator.NotEqual,i18NService.setupMessage("filter.notEqual","Not Equal"));

        operatorStrings.put(JPA.Operator.IsNull,i18NService.setupMessage("filter.isNull","Is Null"));
        operatorStrings.put(JPA.Operator.NotNull,i18NService.setupMessage("filter.notNull","Not Null"));

        operatorStrings.put(JPA.Operator.Greater,i18NService.setupMessage("filter.greater","Greater"));
        operatorStrings.put(JPA.Operator.GreaterOrEqual,i18NService.setupMessage("filter.greaterOrEqual","Greater Or Equal"));

        operatorStrings.put(JPA.Operator.Less,i18NService.setupMessage("filter.less","Less"));
        operatorStrings.put(JPA.Operator.LessOrEqual,i18NService.setupMessage("filter.lessOrEqual","Less Or Equal"));

        operatorStrings.put(JPA.Operator.Between,i18NService.setupMessage("filter.between","Between"));
        operatorStrings.put(JPA.Operator.Like,i18NService.setupMessage("filter.like","Like"));
    }

    public String operatorName(JPA.Operator operator){
        I18NString i18NString = operatorStrings.get(operator);
        return i18NString.value();
    }

    protected void init(List<Attribute<?, ?>> attributes) {
        emf = ApplicationContextUtils.getBean(EntityManagerFactory.class);
        i18NService = ApplicationContextUtils.getBean(I18NService.class);
        entityViewService = ApplicationContextUtils.getBean(EntityViewService.class);
        metamodel = emf.getMetamodel();
        rootType = metamodel.entity(getEntityCls());
        this.attributes = attributes;

        setupOperatorName();

    }

    @Override
    public Component createUI(AbstractOrderedLayout parent)  {

        setupOperatorName();

        EntityRenderConfiguration<E> renderConfiguration = entityViewService.entityRenderConfiguration(getEntityCls());

        Class<?> valueCls = valueType();

        uiContent = new HorizontalLayout();

        uiContent.setSpacing(true);

        uiContent.setWidth("100%");

        checkbox = new CheckBox();

        pActive = new ObjectProperty<>(this.isActive);

        pOperator = new ObjectProperty<>(this.operator);

        pFirst = new ObjectProperty<>(firstValue,(Class<V>) valueType());

        pSecond = new ObjectProperty<>(secondValue,(Class<V>) valueType());

        uiContent.addComponent(checkbox);

        uiContent.setComponentAlignment(checkbox,Alignment.BOTTOM_CENTER);

        String title = fieldName().value();

        Label label = new Label(title);

        label.setWidth("100px");

        uiContent.addComponent(label);

        uiContent.setComponentAlignment(label,Alignment.BOTTOM_CENTER);

        ComboBox comboBox = new ComboBox();

        comboBox.addItem(JPA.Operator.Equal);
        comboBox.setItemCaption(JPA.Operator.Equal,operatorName(JPA.Operator.Equal));

        comboBox.addItem(JPA.Operator.NotEqual);
        comboBox.setItemCaption(JPA.Operator.NotEqual,operatorName(JPA.Operator.NotEqual));

        comboBox.addItem(JPA.Operator.IsNull);
        comboBox.setItemCaption(JPA.Operator.IsNull,operatorName(JPA.Operator.IsNull));

        comboBox.addItem(JPA.Operator.NotNull);
        comboBox.setItemCaption(JPA.Operator.NotNull,operatorName(JPA.Operator.NotNull));

        if (Comparable.class.isAssignableFrom(valueCls)){
            comboBox.addItem(JPA.Operator.Greater);
            comboBox.setItemCaption(JPA.Operator.Greater,operatorName(JPA.Operator.Greater));

            comboBox.addItem(JPA.Operator.GreaterOrEqual);
            comboBox.setItemCaption(JPA.Operator.GreaterOrEqual,operatorName(JPA.Operator.GreaterOrEqual));

            comboBox.addItem(JPA.Operator.Less);
            comboBox.setItemCaption(JPA.Operator.Less,operatorName(JPA.Operator.Less));

            comboBox.addItem(JPA.Operator.LessOrEqual);
            comboBox.setItemCaption(JPA.Operator.LessOrEqual,operatorName(JPA.Operator.LessOrEqual));

            comboBox.addItem(JPA.Operator.Between);
            comboBox.setItemCaption(JPA.Operator.Between,operatorName(JPA.Operator.Between));
        }

        if (String.class.isAssignableFrom(valueCls)){
            comboBox.addItem(JPA.Operator.Like);
            comboBox.setItemCaption(JPA.Operator.Like,operatorName(JPA.Operator.Like));
        }

        comboBox.setNullSelectionAllowed(false);

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                JPA.Operator operator = (JPA.Operator) event.getProperty().getValue();

                if (JPA.Operator.IsNull == operator || JPA.Operator.NotNull == operator){
                    fieldFirst.setVisible(false);
                    fieldSecond.setVisible(false);
                }else if (JPA.Operator.Between == operator){
                    fieldFirst.setVisible(true);
                    fieldSecond.setVisible(true);
                }else{
                    fieldFirst.setVisible(true);
                    fieldSecond.setVisible(false);
                }

            }
        });

        uiContent.addComponent(comboBox);

        //uiContent.setExpandRatio(comboBox,1);

        try {

            EntityRenderConfiguration.FormFieldConfig formFieldConfig = renderConfiguration.formFieldForAttribute(lastAttribute(), true);

            fieldFirst = formFieldConfig.getFieldType().newInstance();

            formFieldConfig.getFormFieldSetup().fieldSetup(fieldFirst, null, uiContent, null);

            fieldSecond = formFieldConfig.getFieldType().newInstance();

            formFieldConfig.getFormFieldSetup().fieldSetup(fieldSecond, null, uiContent, null);

            fieldSecond.setVisible(false);

            uiContent.addComponent(fieldFirst);

            uiContent.addComponent(fieldSecond);

            uiContent.setExpandRatio(fieldFirst,1);
            uiContent.setExpandRatio(fieldSecond,1);

        }catch (Throwable e){
            throw new StatusAndMessageError(-9,e);
        }


        fieldGroup.bind(checkbox, pActive);
        fieldGroup.bind(comboBox,pOperator);

        fieldGroup.bind(fieldFirst,pFirst);
        fieldGroup.bind(fieldSecond,pSecond);

        comboBox.setValue(JPA.Operator.Equal);

        return uiContent;
    }

    @Override
    public Predicate generatePredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<?> path = (Path<V>) pathForAttributes(root);
        Predicate predicate = cb.equal(cb.literal(1),1);

        Class<?> valueCls = valueType();

        if (JPA.Operator.Equal == operator){
            predicate = cb.equal(path,firstValue);
        }else if (JPA.Operator.NotEqual == operator){
            predicate = cb.notEqual(path,firstValue);
        }else if (JPA.Operator.IsNull == operator){
            predicate = cb.isNull(path);
        }else if (JPA.Operator.NotNull == operator){
            predicate = cb.isNotNull(path);
        }

        else if (Comparable.class.isAssignableFrom(valueCls)) {
            Comparable comparableValue = (Comparable) firstValue;
            Path<? extends  Comparable> comparablePath = (Path<? extends Comparable>) path;
            if (JPA.Operator.Greater == operator) {
                predicate = cb.greaterThan(comparablePath, comparableValue);
            }
            else if (JPA.Operator.GreaterOrEqual == operator) {
                predicate = cb.greaterThanOrEqualTo(comparablePath, comparableValue);
            }
            else if (JPA.Operator.Less == operator) {
                predicate = cb.lessThan(comparablePath, comparableValue);
            }
            else if (JPA.Operator.LessOrEqual == operator) {
                predicate = cb.lessThanOrEqualTo(comparablePath, comparableValue);
            }
            else if (JPA.Operator.Between == operator){
                Comparable comparableValue2 = (Comparable) secondValue;
                predicate = cb.between(comparablePath, comparableValue,comparableValue);
            }
        }

        else if (String.class.isAssignableFrom(valueCls)){
            Path<String> stringPath = (Path<String>) path;
            String stringValue = (String) firstValue;
            if (JPA.Operator.Like == operator){
                predicate = cb.like(stringPath,stringValue);
            }
        }

        return predicate;
    }

    public Class<E> getEntityCls() {
        if (eClass == null) {
            Class<? extends SingleAttributeSpecification> thisCls = this.getClass();

            Class<?>[] typeArgs = TypeResolver.resolveRawArguments(SingleAttributeSpecification.class, thisCls);
            if (typeArgs.length > 0) {
                eClass = (Class<E>) typeArgs[0];
            }
        }
        return eClass;
    }
}
