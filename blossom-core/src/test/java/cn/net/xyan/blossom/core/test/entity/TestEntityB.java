package cn.net.xyan.blossom.core.test.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/4/23.
 */
@Entity
@Table(name = "Table_B")
public class TestEntityB {
    String colPK;
    String colA;
    String colB;
    Integer colC;

    List<TestEntityA> entityA = new LinkedList<>();


    public String getColA() {
        return colA;
    }

    public void setColA(String colA) {
        this.colA = colA;
    }

    public String getColB() {
        return colB;
    }

    public void setColB(String colB) {
        this.colB = colB;
    }

    public Integer getColC() {
        return colC;
    }

    public void setColC(Integer colC) {
        this.colC = colC;
    }

    @Id
    public String getColPK() {
        return colPK;
    }

    public void setColPK(String colPK) {
        this.colPK = colPK;
    }

    @OneToMany(cascade={CascadeType.PERSIST,CascadeType.REMOVE},mappedBy = "entityB")
    public List<TestEntityA> getEntityA() {
        return entityA;
    }

    public void setEntityA(List<TestEntityA> entityA) {
        this.entityA = entityA;
    }
}
