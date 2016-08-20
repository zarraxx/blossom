package cn.net.xyan.blossom.platform.intercept.interceptor;

import cn.net.xyan.blossom.platform.intercept.model.EndPoint;

import java.util.Map;

/**
 * Created by zarra on 16/8/19.
 */
public class ExtensionInterceptor extends AbstractMethodInterceptor {
    @Override
    public void setupProperties() {

    }

    @Override
    public boolean throwOutException() {
        return false;
    }

    @Override
    public boolean accept(EndPoint endPoint) {
        return false;
    }

    @Override
    public boolean needExec(Map<String, Object> content, Object result) {
        return false;
    }

    @Override
    public Object exec(Map<String, Object> content, Object result) {
        return null;
    }
}
