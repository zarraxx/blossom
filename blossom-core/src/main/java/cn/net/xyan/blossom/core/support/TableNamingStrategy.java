package cn.net.xyan.blossom.core.support;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Created by zarra on 16/5/17.
 */
public class TableNamingStrategy extends ImprovedNamingStrategy {

    public String addPrefix(String tableName){
        if (tableName.startsWith("tb_"))
            return tableName;
        return "tb_"+tableName;
    }

    public String addColumnPrefix(String columnName){
        if (columnName == null) return null;
        if (columnName.startsWith("c_"))
            return columnName;
        return "c_"+columnName;
    }

    @Override
    public String classToTableName(String className) {
        return super.classToTableName(addPrefix(className));
    }

    @Override
    public String tableName(String tableName) {
        return super.tableName(addPrefix(tableName));
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(addColumnPrefix(joinedColumn),joinedTable);
        //return super.joinKeyColumnName(joinedColumn, joinedTable);
    }

    @Override
    public String columnName(String columnName) {
        return super.columnName(addColumnPrefix(columnName));
    }

    @Override
    public String propertyToColumnName(String propertyName) {
       return super.propertyToColumnName(addColumnPrefix(propertyName));
    }
}
