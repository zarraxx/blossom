package cn.net.xyan.blossom.core.jpa.entity;

import javax.persistence.*;

/**
 * Created by xiashenpin on 16/1/18.
 */
@Entity
@Table(name = "sys_ez_sequence_number", uniqueConstraints = { @UniqueConstraint(columnNames = { "sequence_name" }) })
public class SequenceNumber
{
    @Id
    @Column(name = "sequence_name", updatable = false)
    private String className;

    @Column(name = "nextValue")
    private Long nextValue = 1L;

    @Column(name = "increment_size")
    private Long incrementValue = 10L;

    @Column(name = "step")
    private Long step = 1L;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getNextValue() {
        return nextValue;
    }

    public void setNextValue(Long nextValue) {
        this.nextValue = nextValue;
    }

    public Long getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(Long incrementValue) {
        this.incrementValue = incrementValue;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }
}