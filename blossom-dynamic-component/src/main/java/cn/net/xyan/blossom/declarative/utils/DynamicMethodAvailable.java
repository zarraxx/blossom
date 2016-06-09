package cn.net.xyan.blossom.declarative.utils;

import cn.net.xyan.blossom.declarative.ui.RuntimeContext;

/**
 * Created by zarra on 16/6/9.
 */
public interface DynamicMethodAvailable {

    String PROXYSETTER = "DynamicMethodAvailableSetProxy";
    String PROXYGETTER = "DynamicMethodAvailableGetProxy";

    String CONTEXTSETTER = "DynamicMethodAvailableSetRuntimeContext";
    String CONTEXTGETTER = "DynamicMethodAvailableGetRuntimeContext";

    void DynamicMethodAvailableSetProxy(DynamicMethodProxy proxy);
    DynamicMethodProxy DynamicMethodAvailableGetProxy();

    void DynamicMethodAvailableSetRuntimeContext(RuntimeContext runtimeContext);
    RuntimeContext DynamicMethodAvailableGetRuntimeContext();
}
