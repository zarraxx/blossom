package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.platform.dao.RequestLogDao;
import cn.net.xyan.blossom.platform.entity.log.RequestLog;
import cn.net.xyan.blossom.platform.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by zarra on 16/8/20.
 */
public class LogServiceImpl implements LogService {

    @Autowired
    RequestLogDao requestLogDao;

    Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    @Async
    public void saveLog(RequestLog log) {
        logger.info("aysnc save log");
        requestLogDao.saveAndFlush(log);
    }
}
