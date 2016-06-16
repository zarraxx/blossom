package cn.net.xyan.blossom.declarative.script;

import org.mozilla.javascript.Scriptable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zarra on 16/6/9.
 */
public class RuntimeContext   {

    //public static final String KEYForSCOPE = "__scope__";
    //public static final String KEYForWriteable = "__writeable__";

    Map<String,Object> root;

    Map<String,Object>  variables;

    Scriptable scope;



    public <M extends Map<String,Object> > RuntimeContext(Class<M> cls,boolean createVariables){
        try {
            Map<String, Object> root = cls.newInstance();
            this.root = root;

            if (createVariables) {

                variables = cls.newInstance();
                //this.root.put(KEYForWriteable,variables);
            }

        } catch (InstantiationException|IllegalAccessException e) {
            throw new NoSuchMethodError("the map class must have defaule constructor");
        }
    }

    public <M extends Map<String,Object> > RuntimeContext(Class<M> cls) {
        this(cls,true);
    }

    public RuntimeContext(){
        this(HashMap.class);
    }

    public Object get(String key){
        return root.get(key);
    }

    public void  put(String key,Object v){
        root.put(key,v);
    }

    public Set<String> keySet(){
        return root.keySet();
    }

    public Object getVariable(String key){
        return variables.get(key);
    }

    public void  putVariable(String key,Object v){
        variables.put(key,v);
    }

    public Set<String> variableKeySet(){
        return variables.keySet();
    }

    public Scriptable getScope() {
        return scope;
    }

    public void setScope(Scriptable scope) {
        this.scope = scope;
    }
}
