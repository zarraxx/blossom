package cn.net.xyan.blossom.platform.ui.view.debug;


import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.i18n.TR;
import cn.net.xyan.blossom.core.support.BlossomHibernateLazyLoadingDelegate;
import cn.net.xyan.blossom.core.support.SpringEntityManagerProviderFactory;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.ReflectUtils;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.addon.jpacontainer.EntityManagerProvider;

import com.vaadin.addon.jpacontainer.LazyLoadingDelegate;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zarra on 16/6/6.
 */
@SpringView(name = "debug.JPQL")
@SideBarItem(sectionId = UISystemService.CatalogDebug, caption = "JPQL", order = 1)
@FontAwesomeIcon(FontAwesome.CODE)
public class JPQLDebugView extends VerticalLayout implements View,JPQLForm.RunSQLListener {

    @Autowired
    EntityManager entityManager;

    JPQLForm jpqlForm;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Logger logger = LoggerFactory.getLogger(JPQLDebugView.class);



    public static class TupleBeanItem<E> extends TupleItem {

        E entity;

        LazyLoadingDelegate lazyLoadingDelegate;

        BeanItem<E> beanItem;

        public TupleBeanItem(E e, TupleElement<? super E> tupleElement, LazyLoadingDelegate lazyLoadingDelegate) {
            super(null);
            this.entity = e;
            this.lazyLoadingDelegate = lazyLoadingDelegate;
            if (entity != null) {
                this.beanItem = new BeanItem<>(entity);

                ensureLazyLoad();

                if (lazyLoadingDelegate instanceof BlossomHibernateLazyLoadingDelegate){
                    BlossomHibernateLazyLoadingDelegate bld = (BlossomHibernateLazyLoadingDelegate) lazyLoadingDelegate;
                    bld.fetchEntityManager().detach(entity);
                }
            }
            else
                this.beanItem = new BeanItem<E>(null, (Class<E>)tupleElement.getJavaType());

        }

        public void ensureLazyLoad() {
            PropertyDescriptor[] propertyDescriptors = ReflectUtils.getPropertyDescriptors(entity);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Class<?> type = propertyDescriptor.getPropertyType();
                if (Collection.class.isAssignableFrom(type)) {
                    if (lazyLoadingDelegate != null) {
                        lazyLoadingDelegate.ensureLazyPropertyLoaded(entity, propertyDescriptor.getName());
                    }
                }
            }
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

        public TupleItem(Tuple tuple) {
            this.tuple = tuple;
        }

        public static Item createEntityItem(Tuple tuple, EntityManagerProvider entityManagerProvider, LazyLoadingDelegate lazyLoadingDelegate) {
            EntityManager em = entityManagerProvider.getEntityManager();

            TupleElement<?> tupleElement = tuple.getElements().get(0);

            return new TupleBeanItem<>(tuple.get(0), (TupleElement<? super Object>) tupleElement, lazyLoadingDelegate);

        }

        public static Item createItem(Tuple tuple, EntityManagerProvider entityManagerProvider, LazyLoadingDelegate lazyLoadingDelegate) {
            EntityManager em = entityManagerProvider.getEntityManager();
            if (isEntityTuple(tuple, em)) {
                return createEntityItem(tuple,entityManagerProvider,lazyLoadingDelegate);
            } else {
                return new TupleItem(tuple);
            }
        }

        public static boolean isEntityTuple(Tuple tuple, EntityManager em) {
            List<TupleElement<?>> tupleElements = tuple.getElements();
            if (tupleElements.size() == 1) {
                TupleElement tupleElement = tupleElements.get(0);

                Class<?> cls = tupleElement.getJavaType();

                EntityType<?> entityType = null;
                try {
                    entityType = em.getMetamodel().entity(cls);
                } catch (Throwable e) {

                }
//                if ( ! EntityViewServiceImpl.isPrimaryType(cls)){
//                    return true;
//                }
                if (entityType != null) return true;
            }
            return false;
        }

        public static Class<?> typeForEntityTuple(Tuple tuple, EntityManager em) {
            List<TupleElement<?>> tupleElements = tuple.getElements();
            if (tupleElements.size() == 1) {
                TupleElement tupleElement = tupleElements.get(0);

                Class<?> cls = tupleElement.getJavaType();

                return cls;

            }
            return null;
        }


        protected Class<?> propertyType(String alias) {

            if (alias == null) throw new NullPointerException();

            List<TupleElement<?>> tupleElements = tuple.getElements();

            for (TupleElement tupleElement : tupleElements) {
                if (alias.equals(tupleElement.getAlias())) {
                    return tupleElement.getJavaType();
                }
            }

            return null;
        }

        protected Class<?> propertyType(Integer id) {

            if (id == null) throw new NullPointerException();

            List<TupleElement<?>> tupleElements = tuple.getElements();

            TupleElement tupleElement = tupleElements.get(id);

            return tupleElement.getJavaType();
        }

        @Override
        public Property getItemProperty(Object id) {
            Object value = null;
            Class<?> type = null;
            if (id instanceof Integer) {
                Integer iid = (Integer) id;
                value = tuple.get(iid);
                type = propertyType(iid);
            } else if (id instanceof String) {
                String alias = id.toString();
                value = tuple.get(alias);
                type = propertyType(alias);
            }

            if (type == null) throw new NullPointerException();

            return new ObjectProperty(value, type, true);
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            int size = tuple.getElements().size();
            List<Integer> pids = new LinkedList<>();
            for (int i = 0; i < size; i++) {
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

    public static class SimpleContainer<ITEM extends Item> extends AbstractContainer {

        List<ITEM> items;

        ITEM first;

        public SimpleContainer(Collection<ITEM> collection) {
            items = new LinkedList<>();
            items.addAll(collection);
            if (items != null && items.size() > 0) {
                first = items.get(0);
            }
        }

        @Override
        public Item getItem(Object itemId) {
            if (itemId instanceof Integer) {
                Integer id = (Integer) itemId;
                return items.get(id);
            }
            return null;
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            if (size() > 0) {
                return first.getItemPropertyIds();
            }
            return new LinkedList<Object>();
        }

        @Override
        public Collection<?> getItemIds() {
            List<Integer> ids = new LinkedList<>();
            for (int i = 0; i < size(); i++) {
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
            return first.getItemProperty(propertyId).getType();
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public boolean containsId(Object itemId) {
            if (itemId instanceof Integer) {
                Integer id = (Integer) itemId;
                return id < size();
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

    public JPQLDebugView() {
        setSizeFull();
        setSpacing(true);
        setMargin(true);

        Label header = new Label(TR.m("view.debug.jpql.caption", "JPQL Debug"));
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        Label hr = new Label("<hr/>", ContentMode.HTML);

        addComponent(hr);

        jpqlForm = new JPQLForm();

        jpqlForm.addRunListener(this);

        addComponent(jpqlForm);

        jpqlForm.setSizeFull();

        setExpandRatio(jpqlForm,1);
    }

    @Override
    public void run(JPQLForm.RunSQLEvent event) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                PlatformTransactionManager transactionManager = ApplicationContextUtils.getBean(PlatformTransactionManager.class);
                TransactionDefinition def = new DefaultTransactionDefinition();

                TransactionStatus status = transactionManager.getTransaction( def );

                try{
                    runSQL(event);
                    transactionManager.commit( status );
                }catch (Throwable e){
                    transactionManager.rollback(status);

                    UI.getCurrent().access(new Runnable() {
                        @Override
                        public void run() {
                            String msg = e.getMessage();

                            Notification.show("ERROR", msg, Notification.Type.ERROR_MESSAGE);

                            ExceptionUtils.traceError(e,logger);

                            event.form.setError(msg);
                        }
                    });
                }
            }
        });
    }


    public void runSQL(JPQLForm.RunSQLEvent event) {
        String sql = event.sql;
        try {
            long begin = System.currentTimeMillis();
            TypedQuery<Tuple> query =
                    entityManager.createQuery(sql, Tuple.class);

            List<Tuple> results = query.getResultList();


            SpringEntityManagerProviderFactory factory = ApplicationContextUtils.getBean(SpringEntityManagerProviderFactory.class);
            EntityManagerProvider provider = factory.create();
            //LazyHibernateFilter.LazyHibernateEntityManagerProvider provider = new LazyHibernateFilter.LazyHibernateEntityManagerProvider();
            BlossomHibernateLazyLoadingDelegate hibernateLazyLoadingDelegate = new BlossomHibernateLazyLoadingDelegate();

            hibernateLazyLoadingDelegate.setEntityManagerProvider(provider);

            List<Item> items = new LinkedList<>();
            int size = results.size();
            if (size > 0) {
                Tuple tuple = results.get(0);
                boolean isEntity = TupleItem.isEntityTuple(tuple, entityManager);
                for (Tuple t : results) {
                    if (isEntity)
                        items.add(TupleItem.createEntityItem(t,provider,hibernateLazyLoadingDelegate));
                    else
                        items.add(new TupleItem(t));
                }
            }

            SimpleContainer tupleContainer = new SimpleContainer(items);

            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    long end = System.currentTimeMillis();
                    long cost = end - begin;

                    event.form.setResult(tupleContainer,cost);

                }
            });


        } catch (Throwable e) {

            throw new StatusAndMessageError(-99,e);

        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
