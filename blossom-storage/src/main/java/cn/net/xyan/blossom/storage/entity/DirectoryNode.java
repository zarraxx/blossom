package cn.net.xyan.blossom.storage.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by zarra on 2016/10/18.
 */
@Entity
@Table(name = "storage_archive")
@DiscriminatorValue("directory")
public class DirectoryNode  extends Node{

    Set<Node> children;

    @OneToMany(mappedBy = "parent")
    public Set<Node> getChildren() {
        return children;
    }

    public void setChildren(Set<Node> children) {
        this.children = children;
    }
}
