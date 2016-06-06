package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zarra on 16/5/14.
 */
public interface StatusDao extends EasyJpaRepository<StatusAndType,String> {

    List<? extends StatusAndType> findByType(String type);

    StatusAndType findByTypeAndIndex(String type,Integer index);

    @Query("select st from StatusAndType st left join st.title title where st.type = ?1 and title.defaultValue = ?2 ")
    StatusAndType findByTypeAndTitle(String type,String title);

}
