package cn.net.xyan.blossom.platform.entity;

import com.vaadin.navigator.Navigator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by zarra on 16/7/2.
 */
@Entity
@DiscriminatorValue("view")
public class VaadinViewModule extends Module {

    String viewBeanName;
    String viewName;
    String viewClassName;

    String viewParameter;

    @Column(name = "view_class")
    public String getViewClassName() {
        return viewClassName;
    }

    public void setViewClassName(String viewClassName) {
        this.viewClassName = viewClassName;
    }

    @Column(name = "view_name")
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Column(name = "view_param")
    public String getViewParameter() {
        return viewParameter;
    }

    public void setViewParameter(String viewParameter) {
        this.viewParameter = viewParameter;
    }

    @Column(name = "view_bean_name")
    public String getViewBeanName() {
        return viewBeanName;
    }

    public void setViewBeanName(String viewBeanName) {
        this.viewBeanName = viewBeanName;
    }
    
    public void navigateTo(Navigator navigator){
        if (getViewParameter()!=null)
            navigator.navigateTo(getViewName()+"/"+getViewBeanName());
        else
            navigator.navigateTo(getViewName());
    }
}
