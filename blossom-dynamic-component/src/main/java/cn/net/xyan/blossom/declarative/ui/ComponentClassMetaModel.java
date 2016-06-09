package cn.net.xyan.blossom.declarative.ui;

import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import cn.net.xyan.blossom.declarative.utils.MethodMetaModel;
import com.vaadin.ui.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zarra on 16/6/9.
 */
public class ComponentClassMetaModel {
    String name;
    Class<? extends Component> surperClass;
    List<ClassMetaModel.DynamicFieldInfo> fields = new LinkedList<>();

    List<MethodMetaModel> methods = new LinkedList<>();

    public ComponentClassMetaModel() {

    }

    public ComponentClassMetaModel(String name,
                                   Class<? extends Component> surperClass) {
        this.name = name;
        this.surperClass = surperClass;
    }

    public void addField(String fieldName, Class<?> fieldType) {
        getFields().add(new ClassMetaModel.DynamicFieldInfo(fieldName, fieldType));
    }

    public List<ClassMetaModel.DynamicFieldInfo> getFields() {
        return fields;
    }

    public void setFields(List<ClassMetaModel.DynamicFieldInfo> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Component> getSurperClass() {
        return surperClass;
    }

    public void setSurperClass(Class<? extends Component> surperClass) {
        this.surperClass = surperClass;
    }

    public List<MethodMetaModel> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodMetaModel> methods) {
        this.methods = methods;
    }
}
