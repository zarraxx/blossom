package cn.net.xyan.blossom.plan.planTest;

import org.junit.Test;
import org.quartz.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.impl.StdSchedulerFactory;

/**
 * Created by zarra on 16/6/16.
 */
public class QuartzTest {

    public static class PlanJob implements Job{

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            String script = dataMap.getString("script");

            System.out.println(script);
        }
    }

    @Test
    public void doTest() throws SchedulerException, InterruptedException {
        // Grab the Scheduler instance from the Factory
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = newJob(PlanJob.class)
                .withIdentity("testJob","testGroup")
                .usingJobData("script","s1")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);

        scheduler.start();


        Thread.sleep(1000*10);
    }
}
