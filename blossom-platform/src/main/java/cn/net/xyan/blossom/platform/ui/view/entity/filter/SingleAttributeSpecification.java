package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.core.utils.StringUtils;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityRenderConfiguration;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityUtils;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewService;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.UserError;
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

    EntityType<E> rootType;

    public class QueryModel {
        boolean active;

        JPA.Operator operator;

        V firstValue;

        V secondValue;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public V getFirstValue() {
            return firstValue;
        }

        public void setFirstValue(V firstValue) {
            this.firstValue = firstValue;
        }

        public JPA.Operator getOperator() {
            return operator;
        }

        public void setOperator(JPA.Operator operator) {
            this.operator = operator;
        }

        public V getSecondValue() {
            return secondValue;
        }

        public void setSecondValue(V secondValue) {
            this.secondValue = secondValue;
        }
    }

    QueryModel queryModel;

    BeanItem<QueryModel> queryModelBeanItem;


    Class<E> eClass;

    List<Attribute<?, ?>> attributes;

    HorizontalLayout uiContent;

    CheckBox checkbox;

    ComboBox comboBox;

    I18NService i18NService;

    EntityViewService entityViewService;


    AbstractField<V> fieldFirst;

    AbstractField<V> fieldSecond;


    final static List<JPA.Operator> availableOperator =
            Arrays.asList(
                    JPA.Operator.Equal, JPA.Operator.NotEqual,
                    JPA.Operator.IsNull, JPA.Operator.NotNull,
                    JPA.Operator.Greater, JPA.Operator.GreaterOrEqual,
                    JPA.Operator.Less, JPA.Operator.LessOrEqual,
                    JPA.Operator.Between,
                    JPA.Operator.Like);

    Map<JPA.Operator, I18NString> operatorStrings = new HashMap<>();

    public SingleAttributeSpecification(Class<E> eCls, Class<V> vClass, List<Attribute<?, ?>> attributes, JPA.Operator operator) {
        queryModel = new QueryModel();
        this.eClass = eCls;
        queryModel.operator = operator;

        init(attributes);

    }

    public SingleAttributeSpecification(List<Attribute<?, ?>> attributes, JPA.Operator operator) {
        queryModel = new QueryModel();
        queryModel.operator = operator;
        init(attributes);

    }

    public SingleAttributeSpecification(Attribute<E, ?> attribute, JPA.Operator operator) {
        queryModel = new QueryModel();
        queryModel.operator = operator;
        init(Arrays.asList(attribute));
    }

    public Attribute<?, ?> lastAttribute() {
        List<Attribute<?, ?>> attributes = getAttributes();
        return attributes.get(attributes.size() - 1);
    }

    public Class<?> valueType() {
        return EntityUtils.valueTypeForAttribute(lastAttribute());
    }

    public List<Attribute<?, ?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute<?, ?>> attributes) {
        this.attributes = attributes;
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


    public String fieldName(Attribute<?, ?> attribute) {

        ManagedType<?> declareType = attribute.getDeclaringType();
        String key = EntityRenderConfiguration.FormFieldConfig.entityFormFieldCaptionKey(declareType.getJavaType(), attribute.getName());
        I18NString fieldName = i18NService.setupMessage(key, attribute.getName());
        if (fieldName != null)
            return fieldName.value();
        return null;
    }

    public String fieldName() {

        Attribute<?, ?> attribute = lastAttribute();

        String name = fieldName(attribute);

        if (name != null && attributes.size() > 1) {
            attribute = attributes.get(attributes.size() - 2);
            String parentName = fieldName(attribute);
            if (parentName != null)
                name = parentName + "." + name;

        }

        return name;
    }


    protected void setupOperatorName() {
        operatorStrings.put(JPA.Operator.Equal, i18NService.setupMessage("filter.equal", "Equal"));
        operatorStrings.put(JPA.Operator.NotEqual, i18NService.setupMessage("filter.notEqual", "Not Equal"));

        operatorStrings.put(JPA.Operator.IsNull, i18NService.setupMessage("filter.isNull", "Is Null"));
        operatorStrings.put(JPA.Operator.NotNull, i18NService.setupMessage("filter.notNull", "Not Null"));

        operatorStrings.put(JPA.Operator.Greater, i18NService.setupMessage("filter.greater", "Greater"));
        operatorStrings.put(JPA.Operator.GreaterOrEqual, i18NService.setupMessage("filter.greaterOrEqual", "Greater Or Equal"));

        operatorStrings.put(JPA.Operator.Less, i18NService.setupMessage("filter.less", "Less"));
        operatorStrings.put(JPA.Operator.LessOrEqual, i18NService.setupMessage("filter.lessOrEqual", "Less Or Equal"));

        operatorStrings.put(JPA.Operator.Between, i18NService.setupMessage("filter.between", "Between"));
        operatorStrings.put(JPA.Operator.Like, i18NService.setupMessage("filter.like", "Like"));
    }

    public String operatorName(JPA.Operator operator) {
        I18NString i18NString = operatorStrings.get(operator);
        return i18NString.value();
    }

    protected void init(List<Attribute<?, ?>> attributes) {
        //emf = ApplicationContextUtils.getBean(EntityManagerFactory.class);
        i18NService = ApplicationContextUtils.getBean(I18NService.class);
        entityViewService = ApplicationContextUtils.getBean(EntityViewService.class);
        // metamodel = emf.getMetamodel();
        rootType = EntityUtils.metamodel().entity(getEntityCls());
        this.attributes = attributes;

        setupOperatorName();

    }

    @Override
    public boolean isActive() {
        return queryModel.isActive();
    }

    @Override
    public void setActive(boolean active) {
        queryModel.setActive(active);
    }

    @Override
    public Component createUI(AbstractOrderedLayout parent) {

        setupOperatorName();

        EntityRenderConfiguration<E> renderConfiguration = entityViewService.entityRenderConfiguration(getEntityCls());

        Class<?> valueCls = valueType();

        uiContent = new HorizontalLayout();

        uiContent.setSpacing(true);

        uiContent.setWidth("100%");

        checkbox = new CheckBox();

        queryModelBeanItem = new BeanItem<QueryModel>(queryModel);

        uiContent.addComponent(checkbox);

        uiContent.setComponentAlignment(checkbox, Alignment.BOTTOM_CENTER);

        String title = fieldName();

        Label label = new Label(title);

        label.setWidth("100px");

        uiContent.addComponent(label);

        uiContent.setComponentAlignment(label, Alignment.BOTTOM_CENTER);

        comboBox = new ComboBox();

        comboBox.addItem(JPA.Operator.Equal);
        comboBox.setItemCaption(JPA.Operator.Equal, operatorName(JPA.Operator.Equal));

        comboBox.addItem(JPA.Operator.NotEqual);
        comboBox.setItemCaption(JPA.Operator.NotEqual, operatorName(JPA.Operator.NotEqual));

        comboBox.addItem(JPA.Operator.IsNull);
        comboBox.setItemCaption(JPA.Operator.IsNull, operatorName(JPA.Operator.IsNull));

        comboBox.addItem(JPA.Operator.NotNull);
        comboBox.setItemCaption(JPA.Operator.NotNull, operatorName(JPA.Operator.NotNull));

        if (Comparable.class.isAssignableFrom(valueCls)) {
            comboBox.addItem(JPA.Operator.Greater);
            comboBox.setItemCaption(JPA.Operator.Greater, operatorName(JPA.Operator.Greater));

            comboBox.addItem(JPA.Operator.GreaterOrEqual);
            comboBox.setItemCaption(JPA.Operator.GreaterOrEqual, operatorName(JPA.Operator.GreaterOrEqual));

            comboBox.addItem(JPA.Operator.Less);
            comboBox.setItemCaption(JPA.Operator.Less, operatorName(JPA.Operator.Less));

            comboBox.addItem(JPA.Operator.LessOrEqual);
            comboBox.setItemCaption(JPA.Operator.LessOrEqual, operatorName(JPA.Operator.LessOrEqual));

            comboBox.addItem(JPA.Operator.Between);
            comboBox.setItemCaption(JPA.Operator.Between, operatorName(JPA.Operator.Between));
        }

        if (String.class.isAssignableFrom(valueCls)) {
            comboBox.addItem(JPA.Operator.Like);
            comboBox.setItemCaption(JPA.Operator.Like, operatorName(JPA.Operator.Like));
        }

        comboBox.setNullSelectionAllowed(false);

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                JPA.Operator operator = (JPA.Operator) event.getProperty().getValue();

                if (JPA.Operator.IsNull == operator || JPA.Operator.NotNull == operator) {
                    fieldFirst.setVisible(false);
                    fieldSecond.setVisible(false);
                } else if (JPA.Operator.Between == operator) {
                    fieldFirst.setVisible(true);
                    fieldSecond.setVisible(true);
                } else {
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
            if (formFieldConfig.getFormFieldSetup()!=null)
                formFieldConfig.getFormFieldSetup().fieldSetup(fieldFirst, null, uiContent, null);

            fieldSecond = formFieldConfig.getFieldType().newInstance();
            if (formFieldConfig.getFormFieldSetup()!=null)
                formFieldConfig.getFormFieldSetup().fieldSetup(fieldSecond, null, uiContent, null);

            fieldSecond.setVisible(false);

            uiContent.addComponent(fieldFirst);

            uiContent.addComponent(fieldSecond);

            uiContent.setExpandRatio(fieldFirst, 1);
            uiContent.setExpandRatio(fieldSecond, 1);

        } catch (Throwable e) {
            throw new StatusAndMessageError(-9, e);
        }

        fieldGroup.setItemDataSource(queryModelBeanItem);

        fieldGroup.bind(checkbox, "active");
        fieldGroup.bind(comboBox, "operator");

        fieldGroup.bind(fieldFirst, "firstValue");
        fieldGroup.bind(fieldSecond, "secondValue");

        comboBox.setValue(JPA.Operator.Equal);

        return uiContent;
    }

    @Override
    public Predicate generatePredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<?> path = (Path<V>) EntityUtils.pathForAttributes(getAttributes(), root);
        Predicate predicate = cb.equal(cb.literal(1), 1);

        Class<?> valueCls = valueType();

        if (JPA.Operator.Equal == queryModel.operator) {
            predicate = cb.equal(path, queryModel.firstValue);
        } else if (JPA.Operator.NotEqual == queryModel.operator) {
            predicate = cb.notEqual(path, queryModel.firstValue);
        } else if (JPA.Operator.IsNull == queryModel.operator) {
            predicate = cb.isNull(path);
        } else if (JPA.Operator.NotNull == queryModel.operator) {
            predicate = cb.isNotNull(path);
        } else if (Comparable.class.isAssignableFrom(valueCls)) {
            Comparable comparableValue = (Comparable) queryModel.firstValue;
            Path<? extends Comparable> comparablePath = (Path<? extends Comparable>) path;
            if (JPA.Operator.Greater == queryModel.operator) {
                predicate = cb.greaterThan(comparablePath, comparableValue);
            } else if (JPA.Operator.GreaterOrEqual == queryModel.operator) {
                predicate = cb.greaterThanOrEqualTo(comparablePath, comparableValue);
            } else if (JPA.Operator.Less == queryModel.operator) {
                predicate = cb.lessThan(comparablePath, comparableValue);
            } else if (JPA.Operator.LessOrEqual == queryModel.operator) {
                predicate = cb.lessThanOrEqualTo(comparablePath, comparableValue);
            } else if (JPA.Operator.Between == queryModel.operator) {
                Comparable comparableValue2 = (Comparable) queryModel.secondValue;
                predicate = cb.between(comparablePath, comparableValue, comparableValue);
            }
        }
        if (String.class.isAssignableFrom(valueCls)) {
            Path<String> stringPath = (Path<String>) path;
            String stringValue = (String) queryModel.firstValue;
            if (JPA.Operator.Like == queryModel.operator) {
                predicate = cb.like(stringPath, stringValue);
            }
        }

        return predicate;
    }

    public boolean isFieldOk(AbstractField<V> field, V value) {

        field.setComponentError(null);
        field.setValidationVisible(false);

        if (field.isVisible()) {
            if (value == null) {
                field.setComponentError(new UserError(TR.m("ui.filter.error.null")));
                throw new StatusAndMessageError(-1, "Value can not be null");
            } else if (value instanceof String) {
                if (StringUtils.isEmpty((String) value)) {
                    field.setComponentError(new UserError(TR.m("ui.filter.error.null")));
                    throw new StatusAndMessageError(-1, "Value can not be null");
                }
            }
        }

        return true;
    }

    @Override
    public boolean inputOk() {
        try {
            getFieldGroup().commit();
            if (isActive()) {
                isFieldOk(fieldFirst, queryModel.firstValue);
                isFieldOk(fieldSecond, queryModel.secondValue);
            }
        } catch (FieldGroup.CommitException e) {
            return false;
        } catch (Throwable e) {
            return false;
        }
        return true;
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
