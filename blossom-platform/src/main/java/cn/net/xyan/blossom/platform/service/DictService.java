package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;

import java.util.List;

/**
 * Created by zarra on 16/6/6.
 */
public interface DictService {

    <ST extends StatusAndType> ST setupStatus(Class<ST> cls,Integer index,String title);

    <ST extends StatusAndType> ST setupStatus(Class<ST> cls,Integer index,String title,boolean abandon);

    <ST extends StatusAndType> ST findStatus(Class<ST> cls,Integer index);

    <ST extends StatusAndType> ST findStatus(Class<ST> cls,String title);

    <ST extends StatusAndType> List<ST> findStatus(Class<ST> cls);
}
