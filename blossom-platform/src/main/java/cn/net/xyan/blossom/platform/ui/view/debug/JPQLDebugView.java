package cn.net.xyan.blossom.platform.ui.view.debug;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.service.EntityViewServiceImpl;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/6.
 */
@SpringView(name = "debug.JPQL")
@SideBarItem(sectionId = UISystemService.CatalogDebug, caption = "JPQL", order = 1)
@FontAwesomeIcon(FontAwesome.CODE)
public class JPQLDebugView extends VerticalLayout implements View {

    TextArea textArea;

    Table table;

    Button bRun;

    @Autowired
    EntityManager entityManager;

    public static class TupleBeanItem<E> extends TupleItem{

        E entity;

        BeanItem<E> beanItem;

        public TupleBeanItem(E e,TupleElement<? super  E> tupleElement) {
            super(null);
            this.entity = e;
            this.beanItem = new BeanItem<E>(entity);
        }

        @Override
        public Property getItemProperty(Object id) {
            return beanItem.getItemProperty(id);
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            return beanItem.getItemPropertyIds();
        }
    }

    public static class TupleItem implements Item {

        Tuple tuple;

        public TupleItem(Tuple tuple){

            this.tuple = tuple;
        }



        public static  boolean isEntityTuple(Tuple tuple){
            List<TupleElement<?>> tupleElements = tuple.getElements();
            if (tupleElements.size() ==1){
                TupleElement tupleElement = tupleElements.get(0);

                Class<?> cls = tupleElement.getJavaType();

                if ( ! EntityViewServiceImpl.isPrimaryType(cls)){
                    return true;
                }
            }
            return false;
        }

        public static TupleItem createItem(Tuple tuple){
            if(isEntityTuple(tuple)){
                TupleElement<?> tupleElement = tuple.getElements().get(0);

                return new TupleBeanItem<Object>(tuple.get(0), (TupleElement<Object>) tupleElement);
            }else {
                return new TupleItem(tuple);
            }
        }

        protected Class<?> propertyType(String alias){

            if (alias == null) throw new NullPointerException();

            List<TupleElement<?>> tupleElements = tuple.getElements();

            for (TupleElement tupleElement :tupleElements){
                if(alias.equals(tupleElement.getAlias())){
                    return tupleElement.getJavaType();
                }
            }

            return null;
        }

        protected Class<?> propertyType(Integer id){

            if (id == null) throw new NullPointerException();

            List<TupleElement<?>> tupleElements = tuple.getElements();

            TupleElement tupleElement = tupleElements.get(id);

            return tupleElement.getJavaType();
        }

        @Override
        public Property getItemProperty(Object id) {
            Object value = null;
            Class<?> type = null;
            if (id instanceof Integer){
                Integer iid = (Integer) id;
                value = tuple.get(iid);
                type = propertyType(iid);
            }else if (id instanceof String){
                String alias = id.toString();
                value = tuple.get(alias);
                type = propertyType(alias);
            }

            if (type == null) throw new NullPointerException();

            return new ObjectProperty(value,type,true);
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            int size = tuple.getElements().size();
            List<Integer> pids = new LinkedList<>();
            for (int i=0;i<size;i++){
                pids.add(i);
            }
            return pids;
        }

        @Override
        public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("addItemProperty is not support");
        }

        @Override
        public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("removeItemProperty is not support");
        }
    }

    public static class TupleContainer extends AbstractContainer{

        Collection<Tuple> tuples;

        TupleItem tupleItem ;

        List<TupleItem> tupleItems = new LinkedList<>();

        public TupleContainer(Collection<Tuple> tuples){
            this.tuples = tuples;
            for (Tuple tuple : tuples){


                TupleItem item =  TupleItem.createItem(tuple);
                tupleItems.add(item);

                if (tupleItem == null) tupleItem = item;
            }
        }

        @Override
        public Item getItem(Object itemId) {
            if (itemId instanceof Integer){
                Integer id = (Integer) itemId;
                return tupleItems.get(id);
            }
            return null;
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            if (size()>0){
                return tupleItem.getItemPropertyIds();
            }
            return new LinkedList<Object>();
        }

        @Override
        public Collection<?> getItemIds() {
            List<Integer> ids = new LinkedList<>();
            for (int i=0;i<size();i++){
                ids.add(i);
            }
            return ids;
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            TupleItem item = (TupleItem) getItem(itemId);
            return item.getItemProperty(propertyId);
        }

        @Override
        public Class<?> getType(Object propertyId) {
            return tupleItem.getItemProperty(propertyId).getType();
        }

        @Override
        public int size() {
            return tupleItems.size();
        }

        @Override
        public boolean containsId(Object itemId) {
            if (itemId instanceof Integer){
                Integer id = (Integer) itemId;
                return id<size();
            }
            return false;
        }

        @Override
        public Item addItem(Object itemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object addItem() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeItem(Object itemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAllItems() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    public JPQLDebugView(){
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.debug.jpql.caption","JPQL Debug"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        Label hr = new Label("<hr/>", ContentMode.HTML);

        addComponent(hr);

        textArea = new TextArea("JPQL:");

        textArea.setWidth("100%");

        bRun = new Button(TR.m(TR.Run,"Run"));

        addComponent(textArea);

        addComponent(bRun);

        table = new Table("Result:");

        table.setSizeFull();

        addComponent(table);

        setExpandRatio(table,1);

        bRun.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String sql = textArea.getValue();

                runSQL(sql);
            }
        });

    }

    @Transactional
    public void runSQL(String jpql){
        try{
            bRun.setComponentError(null);
            TypedQuery<Tuple> query =
                    entityManager.createQuery(jpql, Tuple.class);

            List<Tuple> results = query.getResultList();

            TupleContainer tupleContainer = new TupleContainer(results);

            table.setContainerDataSource(tupleContainer);



            table.refreshRowCache();

        }catch (Throwable e){

            String msg = e.getMessage();

            Notification.show("ERROR",msg,Notification.Type.ERROR_MESSAGE);

            throw new StatusAndMessageError(-100,e);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
