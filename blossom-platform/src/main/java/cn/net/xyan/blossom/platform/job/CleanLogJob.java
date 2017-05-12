package cn.net.xyan.blossom.platform.job;

import cn.net.xyan.blossom.platform.dao.RequestLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * Created by zarra on 2017/5/12.
 */
public class CleanLogJob {

    int remainDays = 30;

    @Autowired
    RequestLogDao requestLogDao;

    public int getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void doJob(){
        Date now = new Date();

        Date begin = new Date(now.getTime() - getRemainDays() * 3600L*24L*1000L);

        requestLogDao.deleteBeforeTimeStamp(begin);
    }
}
