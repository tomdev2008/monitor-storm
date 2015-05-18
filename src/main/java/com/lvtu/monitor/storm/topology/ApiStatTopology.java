package com.lvtu.monitor.storm.topology;

import org.nutz.ioc.loader.annotation.IocBean;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

import com.lvtu.monitor.storm.bolts.ApiAccessCounter;
import com.lvtu.monitor.storm.bolts.ApiIpCounter;
import com.lvtu.monitor.storm.spouts.NginxLogReader;

/**
 * @Title: ApiStatTopology.java
 * @Package com.lvtu.monitor.storm.topology
 * @Description: 统计api接口访问的拓扑任务
 * @author lvzimin
 * @date 2015年5月15日 下午1:45:51
 * @version V1.0.0
 */
@IocBean(singleton = true)
public class ApiStatTopology {

	/**
	 * topology名称
	 * @return
	 */
	public String getName() {
		return "apiStatTopology";
	}

	/**
	 * topology配置
	 * @return
	 */
	public Config getConfig() {
		Config conf = new Config();
		conf.setDebug(true);
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		return conf;
	}

	/**
	 * topology的
	 * @return
	 */
	public StormTopology getTopology() {
		TopologyBuilder builder = new TopologyBuilder();
		NginxLogReader logReader = new NginxLogReader();
		ApiAccessCounter accessCounter = new ApiAccessCounter();
		ApiIpCounter ipCounter = new ApiIpCounter();
		builder.setSpout("logReader", logReader);
		builder.setBolt("accessCounter", accessCounter, 1).shuffleGrouping(
				"logReader");
		builder.setBolt("ipCounter", ipCounter, 1).shuffleGrouping(
				"logReader");
		return builder.createTopology();
	}

}
