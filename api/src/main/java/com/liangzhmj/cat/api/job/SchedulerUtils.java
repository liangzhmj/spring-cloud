package com.liangzhmj.cat.api.job;

import com.liangzhmj.cat.tools.date.DateUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;


/**
 * 任务调度工具类
 *
 * @author liangzhmj
 */
@Log4j2
public class SchedulerUtils {

    /**
     * 参数key
     */
    public static String PARAMS_KEY = "params_key";
    public static String CRON_ID = "cron_id_";

    private static SchedulerUtils schedulerUtils;
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    /**
     * 单例
     */
    private SchedulerUtils() {
        try {
            this.init();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     *
     * @throws
     */
    private void init() throws SchedulerException {
        if (schedulerFactory == null) {
            schedulerFactory = new StdSchedulerFactory();
        }
        if (scheduler == null) {
            scheduler = schedulerFactory.getScheduler();
        }
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SchedulerUtils getInstance() {
        if (schedulerUtils != null) {
            return schedulerUtils;
        }
        synchronized (SchedulerUtils.class) {
            if (schedulerUtils == null) {
                schedulerUtils = new SchedulerUtils();
            }
        }
        return schedulerUtils;
    }


    /**
     * 添加运行任务
     *
     * @param myjob
     * @return 0:添加失败，1：添加成功
     */
    public int startJob(SynaJob myjob) {
        try {
            this.init();
//            JobDetail jobDetail = new JobDetail(myjob.getId(),Scheduler.DEFAULT_GROUP, myjob.getClass());
            JobDetail jobDetail = JobBuilder.newJob(myjob.getClass()).withIdentity(myjob.getId(), Scheduler.DEFAULT_GROUP).build();
            //传递参数
            if (!StringUtils.isEmpty(myjob.getParams())) {
                jobDetail.getJobDataMap().put(SchedulerUtils.PARAMS_KEY, myjob.getParams());
            }
            //cron表达式的触发器
//            CronTrigger trigger = new CronTrigger(SchedulerUtils.CRON_ID+myjob.getId(),Scheduler.DEFAULT_GROUP);
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(SchedulerUtils.CRON_ID + myjob.getId(), Scheduler.DEFAULT_GROUP);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(myjob.getCron()));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();

//            trigger.setCronExpression(myjob.getCron());
            //启动任务
            Date date = scheduler.scheduleJob(jobDetail, trigger);
            log.info("任务【" + myjob.getName() + "】于[" + DateUtils.dateToString("yyyy-MM-dd HH:mm:ss", date) + "]启动");
            return 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }


    /**
     * 删除任务
     *
     * @param jobId
     * @return 0:删除失败 1:删除成功
     */
    public int stopJob(String jobId) {
        boolean isOk = false;
        try {
            isOk = scheduler.deleteJob(JobKey.jobKey(jobId, Scheduler.DEFAULT_GROUP));// 删除任务
            if (isOk) {
                log.info("任务【" + jobId + "】于[" + DateUtils.getCurrentStr("yyyy-MM-dd HH:mm:ss") + "]移除");
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        return isOk ? 1 : 0;
    }

    /**
     * 停止调度
     *
     * @return
     */
    public void shutdown() {
        try {
            if (scheduler != null && scheduler.isStarted()) {
                scheduler.shutdown();
                scheduler = null;
                log.info(DateUtils.getCurrentStr("yyyy-MM-dd HH:mm:ss") + "停止调度");
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }

}
