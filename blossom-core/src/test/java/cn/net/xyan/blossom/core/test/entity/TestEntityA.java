package cn.net.xyan.blossom.core.test.entity;

import javax.persistence.*;

/**
 * Created by zarra on 16/4/23.
 */
@Entity
@Table(name = "Table_A")
public class TestEntityA {
    String name;
    String value;

    TestEntityB entityB;

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    @JoinColumn(name = "entity_b")
    public TestEntityB getEntityB() {
        return entityB;
    }

    public void setEntityB(TestEntityB entityB) {
        this.entityB = entityB;
    }
}
