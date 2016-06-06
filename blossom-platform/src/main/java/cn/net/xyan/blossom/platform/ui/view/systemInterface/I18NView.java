package cn.net.xyan.blossom.platform.ui.view.systemInterface;

import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by zarra on 16/6/2.
 */
@SpringView(name = "interface.i18n")
@SideBarItem(sectionId = UISystemService.CatalogInterface, caption = "I18NString", order = 0)
@FontAwesomeIcon(FontAwesome.LANGUAGE)
public class I18NView extends VerticalLayout implements View {

    JPAContainer<I18NString> container;

    Table table;

    Logger logger = LoggerFactory.getLogger(I18NView.class);

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    I18NService i18NService;

    Button bEdit;

    Button bSave;

    Map<String, Map<String,String>> dirtyCache = new HashMap<>();

    private void putToDirty(String itemID,String language,String value){
        Map<String,String> item = dirtyCache.get(itemID);
        if (item == null){
            item = new HashMap<>();
            dirtyCache.put(itemID,item);
        }

        item.put(language,value);
    }

    private void setTableEditable(boolean editable){
        if (editable){
            bSave.setEnabled(true);
            bEdit.setCaption(TR.m(TR.Cancel,"Cancel"));
            bEdit.setIcon(FontAwesome.REMOVE);
        }else{
            bSave.setEnabled(false);
            bEdit.setCaption(TR.m(TR.Edit,"Edit"));
            bEdit.setIcon(FontAwesome.EDIT);
        }

        table.setEditable(editable);
    }

    public I18NView(){

        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.i18n.string.caption","Manager I18NString!"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.setSpacing(true);

        bEdit = new Button(TR.m(TR.Edit,"Edit"));
        bSave = new Button(TR.m(TR.Save,"Save"));

        bEdit.setIcon(FontAwesome.EDIT);
        bSave.setIcon(FontAwesome.SAVE);
        bSave.setEnabled(false);

        bEdit.setStyleName(ValoTheme.BUTTON_PRIMARY);
        bSave.setStyleName(ValoTheme.BUTTON_PRIMARY);

        horizontalLayout.addComponent(bEdit);
        horizontalLayout.addComponent(bSave);

        bEdit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                boolean editable = table.isEditable();
                editable = !editable;

                setTableEditable(editable);

            }
        });

        bSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //table.refreshRowCache();

                for (String itemID:dirtyCache.keySet()){
                    I18NString entity = container.getItem(itemID).getEntity();

                    Map<String,String> item = dirtyCache.get(itemID);

                    for (String language:item.keySet()){
                        String value = item.get(language);
                        if ("".equals(language)){
                            entity.setDefaultValue(value);
                        }else{
                            Locale locale = Locale.forLanguageTag(language);
                            entity.putValue(locale,value);
                        }
                    }

                    container.addEntity(entity);

                }
                container.refresh();
                dirtyCache.clear();

                setTableEditable(false);

            }
        });

        addComponent(horizontalLayout);

        table = new Table();

        table.setSizeFull();


        table.setTableFieldFactory(new TableFieldFactory() {
            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                String tag = propertyId.toString();
                String key = itemId.toString();
                Locale locale = Locale.forLanguageTag(tag);

                if ("values".equals(tag)){
                    return new ComboBox();
                }

                TextField textField = new TextField();

                textField.setSizeFull();

                if ("key".equals(tag)){
                    textField.setValue(key);
                    textField.setEnabled(false);
                }

                else if ("defaultValue".equals(tag)){
                    String defaultValue = i18NService.getDefaultMessage(key);
                    textField.setValue(defaultValue);
                    textField.setImmediate(true);
                    textField.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            putToDirty(itemId.toString(),"",event.getProperty().getValue().toString());
                            //dirtyItemIds.add(itemId.toString());
                        }
                    });
                }

                else if ("values".equals(tag)){
                    return new ComboBox();
                }

                else if (i18NService!=null) {
                    String value = i18NService.i18nMessage(key,locale);
                    textField.setValue(value);
                }
                return textField;
            }
        });

        addComponent(table);

        setExpandRatio(table,1);

    }

    public void removeLanguageColumn(Language language){
        String tag = language.getTitle();
        Table.ColumnGenerator columnGenerator = table.getColumnGenerator(tag);
        if (columnGenerator!=null){
            table.removeGeneratedColumn(tag);
        }
    }
    public void addLanguageColumn(final Language language){
        String tag = language.getTitle();
        removeLanguageColumn(language);

//        table.addContainerProperty(tag,String.class,null);

        table.addGeneratedColumn(tag, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                String value = i18NService.i18nMessage((String) itemId, language);
                if (source.isEditable()){
                    TextField textField = new TextField();
                    textField.setSizeFull();
                    textField.setValue(value);
                    textField.setImmediate(true);

                    EntityItem<I18NString> i18nStringItem =  container.getItem(itemId);

                    textField.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            String newValue = event.getProperty().getValue().toString();

                            putToDirty(itemId.toString(),language.getTitle(),newValue);

                        }
                    });

                    return textField;
                }else {
                    Label label = new Label();
                    if (i18NService != null) {

                        label.setValue(value);
                    }
                    return label;
                }

            }
        });
    }

    @Override
    public void attach() {
        super.attach();

        container = EntityContainerFactory.jpaContainer(I18NString.class);
        table.setContainerDataSource(container);


        List<String> visibleColumnNames = new LinkedList<>();

        visibleColumnNames.add("key");
        visibleColumnNames.add("defaultValue");

        for (Language language : i18NService.allLanguage(true)){
            addLanguageColumn(language);
            visibleColumnNames.add(language.getTitle());
        }

        table.setVisibleColumns(visibleColumnNames.toArray(new Object[0]));

        //table.setEditable(true);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
