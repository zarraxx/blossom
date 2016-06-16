package cn.net.xyan.blossom.plan.service;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.core.utils.JsonUtils;
import com.fasterxml.jackson.databind.JavaType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zarra on 16/6/16.
 */
public abstract class JobRunner implements Job {

    static public final String KeyTarget = "KeyTarget";

    static public final String KeyAppContextBeans = "KeyAppContextBeans";

    public Logger logger = LoggerFactory.getLogger(JobRunner.class);

    public abstract void runJob(String target, Map<String,Object> context);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String target             = dataMap.getString(KeyTarget);
        String contextStringValue = dataMap.getString(KeyAppContextBeans);


        JavaType mapType = JsonUtils.mapType(String.class);

        try {
            Map<String,String> jobContext = JsonUtils.objectMapper().readValue(contextStringValue,mapType);
            Map<String,Object> runContext = new HashMap<>();

            for (String key:jobContext.keySet()){
                String value = jobContext.get(key);

                Object bean = ApplicationContextUtils.getBean(value);

                runContext.put(key,bean);

            }


            for (String key:dataMap.keySet()){
                if (!KeyTarget.equals(key) && !KeyAppContextBeans.equals(key)) {
                    Object v = dataMap.get(key);
                    runContext.put(key,v);
                }
            }

            runJob(target,runContext);


        } catch (IOException e) {
            ExceptionUtils.traceError(e,logger);
            throw new StatusAndMessageError(-9,e);
        }
    }
}
