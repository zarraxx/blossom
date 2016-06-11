package cn.net.xyan.blossom.platform.ui.view.entity.filter;

import cn.net.xyan.blossom.core.jpa.utils.JPA;

import javax.persistence.metamodel.Attribute;
import java.util.List;

/**
 * Created by zarra on 16/6/11.
 */
public class FilterConfig {

    JPA.Operator operator = JPA.Operator.Equal;

    List<Attribute<?,?>> attributes;

    String name ;

    public FilterConfig(){

    }

    public FilterConfig(List<Attribute<?,?>> attributes){
        this.attributes = attributes;
    }

    public List<Attribute<?, ?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute<?, ?>> attributes) {
        this.attributes = attributes;
    }

    public JPA.Operator getOperator() {
        return operator;
    }

    public void setOperator(JPA.Operator operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
