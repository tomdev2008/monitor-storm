package com.lvtu.monitor.job.task;

import com.lvtu.monitor.TestBase;
import com.lvtu.monitor.job.ElasticIndexJob;

/** 
* @Title: ElasticNgxlogTaskTest.java 
* @Package com.lvtu.monitor.job.task 
* @Description: TODO 
* @author lvzimin 
* @date 2015年5月21日 上午10:34:49 
* @version V1.0.0 
*/
public class ElasticIndexJobTest extends TestBase{

	/**
	 * 并不能用,因为没有setup
	 */
//	@Test
	public void testRunJob() {
		ElasticIndexJob job = ioc.get(ElasticIndexJob.class);
		job.run();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
