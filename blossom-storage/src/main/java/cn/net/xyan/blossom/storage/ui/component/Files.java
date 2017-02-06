package cn.net.xyan.blossom.storage.ui.component;

import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.utils.StringUtils;
import cn.net.xyan.blossom.storage.dao.NodeSpecification;
import cn.net.xyan.blossom.storage.entity.ArchiveNode;
import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.service.StorageService;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.ListSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specifications;

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
public class Files extends CustomComponent {

    ListSelect select;

    public static class FileContainer extends AbstractContainer{

        Logger logger = LoggerFactory.getLogger(Directory.DirectoryContainer.class);
        JPAContainer<Node> jpaContainer;
        StorageService storageService;
        String parentID;

        public FileContainer(JPAContainer<Node> jpaContainer,
                             String parentID,
                StorageService storageService){
            this.jpaContainer = jpaContainer;
            this.storageService = storageService;

            Node parent = null;
            if (!StringUtils.isEmpty(parentID))
                parent = jpaContainer.getItem(parentID).getEntity();

            jpaContainer.removeAllContainerFilters();
            Node finalParent = parent;
            jpaContainer.setQueryModifierDelegate(new DefaultQueryModifierDelegate(){
                @Override
                public void filtersWillBeAdded(CriteriaBuilder cb, CriteriaQuery<?> query, List<Predicate> predicates) {
                    Root<Node> root = (Root<Node>) query.getRoots().iterator().next();
                    Specifications<Node> w = Specifications.where(JPA.oneEqualOne());
                    w = w.and(NodeSpecification.specificationByType(ArchiveNode.class));
                    w = w.and(NodeSpecification.specificationByParent(finalParent));
                    Predicate predicate = w.toPredicate(root,query,cb);
                    predicates.add(predicate);
                }
            });
        }

        public List<ArchiveNode> queryArchive(Node parent){
            Specifications<Node> w = Specifications.where(JPA.oneEqualOne());
            w = w.and(NodeSpecification.specificationByParent(parent));
            w = w.and(NodeSpecification.specificationByType(ArchiveNode.class));
            List<Node> rawResult = NodeUtils.query(w,jpaContainer);
            List<ArchiveNode> result = new LinkedList<>();
            for (Node node:rawResult){
                if (node instanceof ArchiveNode &&
                        storageService.canRead(node,storageService.currentUser())){

                    result.add((ArchiveNode) node);
                }
            }

            return result;
        }

        @Override
        public Item getItem(Object itemId) {
            return jpaContainer.getItem(itemId);
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
        public Class<?> getType(Object propertyId) {
            return jpaContainer.getType(propertyId);
        }

        @Override
        public int size() {
            return jpaContainer.size();
        }

        @Override
        public boolean containsId(Object itemId) {
            return jpaContainer.containsId(itemId);
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

    public Files(){
        ListSelect select = new ListSelect("The List");

        setCompositionRoot(select);
    }
}
