package cn.net.xyan.blossom.storage.entity;

import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zarra on 2016/10/18.
 */
@Entity
@Table(name = "storage_node",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parent","title"})
})
@Inheritance(strategy= InheritanceType.JOINED)
@DiscriminatorColumn(name="type",discriminatorType = DiscriminatorType.STRING)
public class Node {
    public static final int READ  = 1;
    public static final int WRITE = 2;

    String uuid;
    String title;
    User   owner;
    Group  ownerGroup;
    Node   parent;

    Integer permissionOwn;
    Integer permissionGroup;
    Integer permissionAll;

    Date createDate;
    Date modifyDate;
    Date visitDate;

    @Id
    @GeneratedValue(
            generator = "system-uuid"
    )
    @GenericGenerator(
            name = "system-uuid",
            strategy = "uuid"
    )
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "title" ,length = 1024,nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "user")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "own_group")
    public Group getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "parent")
    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Column(name = "permission_own",nullable = false)
    public Integer getPermissionOwn() {
        return permissionOwn;
    }

    public void setPermissionOwn(Integer permissionOwn) {
        this.permissionOwn = permissionOwn;
    }

    @Column(name = "permission_group",nullable = false)
    public Integer getPermissionGroup() {
        return permissionGroup;
    }

    public void setPermissionGroup(Integer permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    @Column(name = "permission_all",nullable = false)
    public Integer getPermissionAll() {
        return permissionAll;
    }

    public void setPermissionAll(Integer permissionAll) {
        this.permissionAll = permissionAll;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return uuid.equals(node.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
