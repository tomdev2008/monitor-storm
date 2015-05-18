package com.lvtu.monitor.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.lvtu.monitor.job.task.ElasticNgxlogTask;

/**
 * @Title: ElasticIndexJob.java
 * @Package com.lvtu.monitor.job
 * @Description: TODO
 * @author lvzimin
 * @date 2015年5月15日 下午4:10:08
 * @version V1.0.0
 */
public class ElasticIndexJob {

	private ScheduledExecutorService threadPool;

	public void run() {
		threadPool = Executors.newSingleThreadScheduledExecutor();
		threadPool.scheduleAtFixedRate(new ElasticNgxlogTask(), 0, 10,
				TimeUnit.SECONDS);
	}

}
