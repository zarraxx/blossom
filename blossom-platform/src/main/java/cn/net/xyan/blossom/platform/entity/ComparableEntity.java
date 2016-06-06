package cn.net.xyan.blossom.platform.entity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by zarra on 16/6/5.
 */
@MappedSuperclass
public class ComparableEntity<T extends ComparableEntity> implements Comparable<T>,Serializable{
    Integer sortOrder;

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int compareTo(T o) {
        if (o == null) {
            return 1;
        }
        if (getSortOrder() == null) {
            if (o.getSortOrder() == null) {
                return 0;
            } else {
                return -1;
            }
        }

        Integer sortOrder = getSortOrder();
        Integer sortOrderOther = o.getSortOrder();

        if (sortOrderOther == null)
            return 1;
        return sortOrder.compareTo(o.getSortOrder());
    }
}
