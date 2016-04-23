package cn.net.xyan.blossom.core.jpa.utils.query;

import cn.net.xyan.blossom.core.jpa.annotation.ResultColumn;

import javax.persistence.criteria.JoinType;

/**
 * Created by zarra on 16/4/23.
 */
public class ResultColumnModel {
    String path; //实体类字段名称

    boolean join = false;
    JoinType joinType= JoinType.INNER; //连接类型

    public ResultColumnModel(String path){
        setPath(path);
        if (path.contains(".")){
            setJoin(true);
        }
    }

    public ResultColumnModel(ResultColumn resultColumn){
        if (resultColumn.value().length()>0){
            setPath(resultColumn.value());
        }else if (resultColumn.values().length>0){
            setPath(String.join(".",resultColumn.values()));
        }
        if (path.contains("."))
            setJoin(true);
        else {
            setJoin(resultColumn.join());
        }

        setJoinType(resultColumn.joinType());

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
