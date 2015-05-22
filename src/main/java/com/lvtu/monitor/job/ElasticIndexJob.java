package com.lvtu.monitor.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.nutz.ioc.loader.annotation.IocBean;

import com.lvtu.monitor.job.task.ElasticNgxlogTask;

/**
 * @Title: ElasticIndexJob.java
 * @Package com.lvtu.monitor.job
 * @Description: 更新elastic索引的定时任务
 * @author lvzimin
 * @date 2015年5月15日 下午4:10:08
 * @version V1.0.0
 */
@IocBean(singleton = true)
public class ElasticIndexJob {

	private ScheduledExecutorService threadPool;

	public void run() {
		threadPool = Executors.newSingleThreadScheduledExecutor();
		threadPool.scheduleAtFixedRate(new ElasticNgxlogTask(), 0, 10,
				TimeUnit.SECONDS);
	}

}
