package com.iboxpay.scheduler;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.iboxpay.util.DateUtil;

public class MyJob implements Job {

	private static SchedulerFactory factory = new StdSchedulerFactory();

	// job实例必须要有无参构造器
	public MyJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.err.println("Hellow World! MyJob is executing. " + DateUtil.parseDateToString(new Date()));
	}

	public static void addJobWithCrionTrigger() {
		try {
			// 获得调度程序，已被创建初始化的调度程序能通过生产它的同一工厂获得
			Scheduler scheduler = factory.getScheduler();
			// 通过JobBuidler创建JobDetail
			JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob", "myJob").build();
			// 通过TriggerBuilder创建触发器
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("myJob", "myJob")
					.withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();
			// 指定JobDetail和触发器
			scheduler.scheduleJob(jobDetail, cronTrigger);
			// 启动调度程序
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void modifyJobTime() {
		try {
			Scheduler scheduler = factory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey("myJob", "myJob");
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (null == trigger) {
				return;
			}
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("myJob", "myJob")
					.withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?")).build();
			scheduler.rescheduleJob(triggerKey, cronTrigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addJobWithSimpleTrigger() {
		try {
			Scheduler scheduler = factory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob", "myJob").build();
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity("myJob", "myJob")
					.startAt(DateBuilder.futureDate(5, IntervalUnit.SECOND)).forJob(jobDetail).build();
			scheduler.scheduleJob(jobDetail, trigger);
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		addJobWithCrionTrigger();
		Thread.sleep(5 * 1000);
		modifyJobTime();
	}

}
