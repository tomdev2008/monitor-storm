package com.lvtu.monitor.storm.topology;

import org.nutz.ioc.loader.annotation.IocBean;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

import com.lvtu.monitor.storm.bolts.AbnormalAccessRecorder;
import com.lvtu.monitor.storm.bolts.ApiAccessCounter;
import com.lvtu.monitor.storm.bolts.ApiIpCounter;
import com.lvtu.monitor.storm.spouts.NginxLogReader;
import com.lvtu.monitor.util.Constant;

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
	 * 
	 * @return
	 */
	public String getName() {
		return "apiStatTopology";
	}

	/**
	 * topology配置
	 * 
	 * @return
	 */
	public Config getConfig() {
		Config conf = new Config();
		conf.setDebug(Boolean.valueOf(Constant
				.getValue("api_stat_topology.conf.debug")));
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, Integer.valueOf(Constant
				.getValue("api_stat_topology.conf.max_spout_pending")));
		return conf;
	}

	/**
	 * topology的
	 * 
	 * @return
	 */
	public StormTopology getTopology() {
		TopologyBuilder builder = new TopologyBuilder();
		NginxLogReader logReader = new NginxLogReader();
		ApiAccessCounter accessCounter = new ApiAccessCounter();
		ApiIpCounter ipCounter = new ApiIpCounter();
		AbnormalAccessRecorder abnormalRecorder = new AbnormalAccessRecorder();
		Integer accessCounterNum = Integer.valueOf(Constant
				.getValue("api_access_counter.parallelism_hint"));
		Integer ipCounterNum = Integer.valueOf(Constant
				.getValue("api_ip_counter.parallelism_hint"));
		Integer abnormalRecorderNum = Integer.valueOf(Constant
				.getValue("abnormal_access_recorder.parallelism_hint"));
		builder.setSpout("logReader", logReader);
		builder.setBolt("accessCounter", accessCounter, accessCounterNum)
				.shuffleGrouping("logReader");
		builder.setBolt("abnormalRecorder", abnormalRecorder,
				abnormalRecorderNum).shuffleGrouping("logReader");
		builder.setBolt("ipCounter", ipCounter, ipCounterNum).shuffleGrouping(
				"logReader");
		// builder.setBolt("ipCounter", ipCounter,
		// 1).fieldsGrouping("accessCounter",
		// new Fields("accessCounterOutput"));
		return builder.createTopology();
	}
}
