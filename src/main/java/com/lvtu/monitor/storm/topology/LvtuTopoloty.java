package com.lvtu.monitor.storm.topology;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;

/** 
 * @Title: LvtuTopoloty.java 
 * @Package com.lvtu.monitor.storm.topology 
 * @Description: TODO 
 * @author lvzimin 
 * @date 2015年5月18日 下午5:45:25 
 * @version V1.0.0 
 */
public interface LvtuTopoloty {

	/**
	 * topology名称
	 * @return
	 */
	String getName();

	/**
	 * topology配置
	 * @return
	 */
	Config getConfig();

	/**
	 * topology执行任务
	 * @return
	 */
	StormTopology getTopology();

}
