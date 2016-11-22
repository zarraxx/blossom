package cn.net.xyan.blossom.storage.ui.component;


import cn.net.xyan.blossom.core.support.EntityContainerFactory;
import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.storage.dao.NodeSpecification;
import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.service.StorageService;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;


import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 2016/10/19.
 */
public class Directory extends CustomComponent {

    StorageService storageService;

    Tree tree;

    Logger logger = LoggerFactory.getLogger(Directory.class);

    public static class DirectoryContainer extends AbstractContainer implements Container.Hierarchical {

        Logger logger = LoggerFactory.getLogger(DirectoryContainer.class);
        JPAContainer<Node> jpaContainer;
        StorageService storageService;
        public DirectoryContainer(JPAContainer<Node> jpaContainer, StorageService storageService){
            this.jpaContainer = jpaContainer;
            this.storageService = storageService;
        }

        public JPAContainer<Node> getJpaContainer() {
            return jpaContainer;
        }

        public Node getNode(Object itemID){
            return jpaContainer.getItem(itemID).getEntity();
        }


        public List<Node> query(Specification<Node> specification){
            EntityManager em  = jpaContainer.getEntityProvider().getEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Node> query = builder.createQuery(Node.class);
            Root<Node> root = query.from(Node.class);

            Predicate predicate = specification.toPredicate(root,query,builder);

            query.where(predicate);

            return em.createQuery(query).getResultList();
        }

        public List<Node> queryChildren(Node parent){
            List<Node> rawResult = query(NodeSpecification.specificationByParent(parent));
            List<Node> result = new LinkedList<>();
            for (Node node:rawResult){
                if (storageService.canRead(node,storageService.currentUser())){
                    result.add(node);
                }
            }

            return result;
        }

        @Override
        public Item getItem(Object o) {
            return jpaContainer.getItem(o);
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            return jpaContainer.getContainerPropertyIds();
        }

        @Override
        public Collection<?> getItemIds() {
            return jpaContainer.getItemIds();
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            return jpaContainer.getContainerProperty(itemId,propertyId);
        }

        @Override
        public Class<?> getType(Object o) {
            return jpaContainer.getType(o);
        }

        @Override
        public int size() {
            return jpaContainer.size();
        }

        @Override
        public boolean containsId(Object o) {
            return jpaContainer.containsId(o);
        }

        @Override
        public Item addItem(Object o) throws UnsupportedOperationException {
            return jpaContainer.addItem(o);
        }

        @Override
        public Object addItem() throws UnsupportedOperationException {
            return jpaContainer.addItem();
        }

        @Override
        public boolean removeItem(Object itemId) throws UnsupportedOperationException {
            return jpaContainer.removeItem(itemId);
        }

        @Override
        public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
            return jpaContainer.addContainerProperty(propertyId,type,defaultValue);
        }

        @Override
        public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
            return jpaContainer.removeContainerProperty(propertyId);
        }

        @Override
        public boolean removeAllItems() throws UnsupportedOperationException {
            return jpaContainer.removeAllItems();
        }

        //-----------------------
        @Override
        public Collection<?> getChildren(Object itemId) {
            Node node = getNode(itemId);
            List<String> children = new LinkedList<>();
            if (node instanceof DirectoryNode){
                DirectoryNode directoryNode = (DirectoryNode) node;
                for (Node n : queryChildren(node)){
                    if (n instanceof DirectoryNode)
                        children.add(n.getUuid());
                }
            }
            return children;
        }

        @Override
        public Object getParent(Object itemId) {
            Node node = getNode(itemId);

            return node.getParent() == null?null:node.getParent().getUuid();
        }

        @Override
        public Collection<?> rootItemIds() {
            List<String> rootIds = new LinkedList<>();

            for (Node node:queryChildren(null)){
                if (node instanceof DirectoryNode)
                    rootIds.add(node.getUuid());
            }
            //em.createQuery()
            return rootIds;
        }

        @Override
        public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
            try {
                Node parent = getNode(newParentId);
                Node node = getNode(itemId);

                node.setParent(parent);

                jpaContainer.addEntity(node);
                return true;
            }catch (Throwable e){
                ExceptionUtils.traceError(e,logger);
                return false;
            }

        }

        @Override
        public boolean areChildrenAllowed(Object itemId) {
            Node node = getNode(itemId);

            if (node instanceof DirectoryNode)
                return true;
            else
                return false;
        }

        @Override
        public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
            throw new UnsupportedFilterException();
        }

        @Override
        public boolean isRoot(Object itemId) {
            Node node = getNode(itemId);
            return node.getParent() == null;
        }

        @Override
        public boolean hasChildren(Object itemId) {
            Node node = getNode(itemId);
            List<Node> children = query(NodeSpecification.specificationByParent(node));
            return children.size()>0;
        }


    }


    public Directory(StorageService storageService){
        this.storageService = storageService;
        tree = new Tree();

        JPAContainer<Node> jpaContainer = EntityContainerFactory.jpaContainer(Node.class);

        DirectoryContainer directoryContainer = new DirectoryContainer(jpaContainer,storageService);

        tree.setContainerDataSource(directoryContainer);


        tree.setItemCaptionPropertyId("title");
        tree.setItemCaptionMode( AbstractSelect.ItemCaptionMode.PROPERTY);



        setCompositionRoot(tree);
    }


}
