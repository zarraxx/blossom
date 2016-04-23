package cn.net.xyan.blossom.core.jpa.utils.query;



import cn.net.xyan.blossom.core.jpa.annotation.QueryCondition;
import cn.net.xyan.blossom.core.jpa.utils.JPA;

import javax.persistence.criteria.JoinType;

/**
 * Created by zarra on 16/3/8.
 */
public class QueryConditionModel<T> {
    JPA.Operator operator = JPA.Operator.Equal;
    String path;
    T value;

    boolean ignoreNull = true;//字段为null 时是否忽略此条件

    boolean hasIgnoreValue = false;
    String ignoreValue;//字段为该值时 忽略此条件

    Class<? extends PredicateCreator> creator = DefaultPredicateCreator.class; //条件生成器

    boolean join = false;
    JoinType joinType = JoinType.INNER; //连接类型

    public QueryConditionModel(String path, T t){
        this.path = path;
        this.value = t;
    }

    public void initWithAnnotation(QueryCondition queryCondition){
        setOperator(queryCondition.operator());

        if (queryCondition.value().length()>0){
            setPath(queryCondition.value());
        }else if (queryCondition.values().length>0){
            setPath(String.join(".",queryCondition.values()));
        }

        setCreator(queryCondition.creator());

        setHasIgnoreValue(queryCondition.hasIgnoreValue());
        setIgnoreValue(queryCondition.ignoreValue());

        setIgnoreNull(queryCondition.ignoreNull());

        if (path.contains("."))
            setJoin(true);
        else {
            setJoin(queryCondition.join());
        }


        setJoinType(queryCondition.joinType());


    }

    public JPA.Operator getOperator() {
        return operator;
    }

    public void setOperator(JPA.Operator operator) {
        this.operator = operator;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public boolean isHasIgnoreValue() {
        return hasIgnoreValue;
    }

    public void setHasIgnoreValue(boolean hasIgnoreValue) {
        this.hasIgnoreValue = hasIgnoreValue;
    }

    public String getIgnoreValue() {
        return ignoreValue;
    }

    public void setIgnoreValue(String ignoreValue) {
        this.ignoreValue = ignoreValue;
    }

    public Class<? extends PredicateCreator> getCreator() {
        return creator;
    }

    public void setCreator(Class<? extends PredicateCreator> creator) {
        this.creator = creator;
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        this.join = join;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }
}
