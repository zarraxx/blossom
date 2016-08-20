package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.log.RequestLog;

/**
 * Created by zarra on 16/8/20.
 */
public interface LogService {
    void saveLog(RequestLog log);
}
