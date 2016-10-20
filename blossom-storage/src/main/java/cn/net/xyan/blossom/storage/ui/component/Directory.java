package cn.net.xyan.blossom.storage.ui.component;


import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.service.StorageService;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;
import org.hibernate.jpa.criteria.CriteriaQueryImpl;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 2016/10/19.
 */
public class Directory extends CustomComponent {

    StorageService storageService;

    Tree tree;


    public static class DirectoryContainer extends AbstractContainer implements Container.Hierarchical {

        JPAContainer<Node> jpaContainer;
        public DirectoryContainer(JPAContainer<Node> jpaContainer){
            this.jpaContainer = jpaContainer;
        }

        public JPAContainer<Node> getJpaContainer() {
            return jpaContainer;
        }

        public Node getNode(Object itemID){
            return jpaContainer.getItem(itemID).getEntity();
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
                for (Node n : directoryNode.getChildren()){
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
            EntityManager em  = jpaContainer.getEntityProvider().getEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Node> query = builder.createQuery(Node.class);
            //em.createQuery()
            return null;
        }

        @Override
        public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
            return false;
        }

        @Override
        public boolean areChildrenAllowed(Object itemId) {
            return false;
        }

        @Override
        public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
            return false;
        }

        @Override
        public boolean isRoot(Object itemId) {
            return false;
        }

        @Override
        public boolean hasChildren(Object itemId) {
            return false;
        }


    }


    public Directory(StorageService storageService){
        this.storageService = storageService;
        tree = new Tree();


        setCompositionRoot(tree);
    }


}
