package com.lvtu.monitor;

import org.junit.Before;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * @Title: TestBase.java
 * @Package com.lvtu.monitor.job.task
 * @Description: TODO
 * @author lvzimin
 * @date 2015年5月21日 上午10:15:21
 * @version V1.0.0
 */
public class TestBase {
	
	protected Ioc ioc;

	@Before
	public void init() {
		ComboIocProvider iocProvider = new ComboIocProvider();
		String[] args = new String[] { "*json", "monitor-storm-beans.json",
				"*annotation", "com.lvtu.monitor" };
		ioc = iocProvider.create(null, args);
		
	}
}
