package cn.net.xyan.blossom.core.support;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Created by zarra on 16/5/17.
 */
public class TableNamingStrategy extends ImprovedNamingStrategy {

    public String addPrefix(String tableName){
        return "tb_"+tableName;
    }

    @Override
    public String classToTableName(String className) {
        String tableName = super.classToTableName(className);
        return addPrefix(tableName);
    }

    @Override
    public String tableName(String tableName) {
        String tn = super.tableName(tableName);
        return addPrefix(tn);
    }
}
