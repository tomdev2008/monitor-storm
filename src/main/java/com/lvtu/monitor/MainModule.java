package com.lvtu.monitor;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

import com.lvtu.monitor.setup.StormSetup;

/**
 * @Title: MainModule.java
 * @Package com.lvtu.monitor
 * @Description: nutz框架的主模块
 * @author lvzimin
 * @date 2015年5月14日 下午12:55:48
 * @version V1.0.0
 */
@SetupBy(StormSetup.class)
@Modules(scanPackage = true)
@IocBy(type = ComboIocProvider.class, args = { "*json",
		"monitor-storm-beans.json", "*annotation", "com.lvtu.monitor" })
public class MainModule {

	@At
	@Ok("json")
	public String index() {
		return "index";
	}
}
