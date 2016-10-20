package cn.net.xyan.blossom.storage.entity;

import javax.persistence.*;

/**
 * Created by zarra on 2016/10/18.
 */
@Entity
@Table(name = "storage_archive")
@DiscriminatorValue("archive")
public class ArchiveNode extends Node {
    BinaryDataHolder binaryDataHolder;

    Long size;

    @OneToOne(optional = true)
    @JoinColumn(name = "holder")
    @Basic(fetch = FetchType.LAZY)
    public BinaryDataHolder getBinaryDataHolder() {
        return binaryDataHolder;
    }

    public void setBinaryDataHolder(BinaryDataHolder binaryDataHolder) {
        this.binaryDataHolder = binaryDataHolder;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
