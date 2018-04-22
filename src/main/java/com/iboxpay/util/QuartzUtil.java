package com.iboxpay.util;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.iboxpay.scheduler.MyJob;

public class QuartzUtil {

	private static SchedulerFactory factory = new StdSchedulerFactory();

	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class<? extends Job> jobClass, String cronExpression) {
		try {
			// 获得调度程序，已被创建初始化的调度程序能通过生产它的同一工厂获得，如果被关闭，另外获得一个新的调度程序
			Scheduler scheduler = factory.getScheduler();
			// 通过JobBuidler创建JobDetail
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
			// 通过TriggerBuilder创建触发器
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
			// 如果触发器没有引用任何任务，则会引用传入该方法的任务
			scheduler.scheduleJob(jobDetail, cronTrigger);
			// 启动调度程序
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void modifyJobTime(String triggerName, String triggerGroupName, String cronExpression) {
		try {
			Scheduler scheduler = factory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (null == cronTrigger || StringUtils.isBlank(cronExpression)) {
				return;
			}
			if (!cronTrigger.getCronExpression().equalsIgnoreCase(cronExpression)) {
				CronTrigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
						.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
				// 通过triggerkey删除trigger，再指定一个新的trigger
				scheduler.rescheduleJob(triggerKey, newTrigger);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
		try {
			Scheduler scheduler = factory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			// 停止触发器
			scheduler.pauseTrigger(triggerKey);
			// 移除触发器
			scheduler.unscheduleJob(triggerKey);
			// 删除任务
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void shutdownJobs() {
		try {
			Scheduler scheduler = factory.getScheduler();
			if (!scheduler.isShutdown()) {
				// 关闭调度程序后将不能重启
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		addJob("test", "test", "test2", "test2", MyJob.class, "0/2 * * * * ?");
		Thread.sleep(5 * 1000);
		modifyJobTime("test2", "test2", "0/1 * * * * ?");
		Thread.sleep(3 * 1000);
		shutdownJobs();
	}
}
