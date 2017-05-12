package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.log.RequestLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by zarra on 16/8/19.
 */
public interface RequestLogDao extends EasyJpaRepository<RequestLog,String> {
    @Modifying
    @Query("delete from RequestLog l where l.date < ?1")
    int deleteBeforeTimeStamp(Date timeStamp);
}
