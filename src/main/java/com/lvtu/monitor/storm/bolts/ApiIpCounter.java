package com.lvtu.monitor.storm.bolts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import com.lvtu.monitor.storm.entity.NginxLog;

/** 
 * @Title: ApiIpCounter.java 
 * @Package com.lvtu.monitor.storm.bolts 
 * @Description: TODO 
 * @author lvzimin 
 * @date 2015年5月15日 下午2:50:37 
 * @version V1.0.0 
 */
public class ApiIpCounter extends BaseBasicBolt {
	
	private static final long serialVersionUID = -2037089371354061741L;
	
	private Log log = LogFactory.getLog(this.getClass());

	private Integer taskId;

	private String componentName;

	private Map<String, Integer> counters;

	/**
	 * At the end of the spout (when the cluster is shutdown We will show the
	 * word counters
	 */
	@Override
	public void cleanup() {
		log.info("-- Word Counter [" + componentName + "-" + taskId
				+ "] --");
		for (Map.Entry<String, Integer> entry : counters.entrySet()) {
			log.info(entry.getKey() + ": " + entry.getValue());
		}
	}

	/**
	 * On create
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		this.counters = new HashMap<String, Integer>();
		this.componentName = context.getThisComponentId();
		this.taskId = context.getThisTaskId();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		NginxLog logEntity = (NginxLog) input.getValue(0);
		if (logEntity == null) {
			return;
		}
		log.info("ip:" + logEntity.getAddr());
//		String key = logEntity.getMethod() + "_" + logEntity.getVersion();
//		if (!counters.containsKey(key)) {
//			counters.put(key, 1);
//		} else {
//			Integer currentNum = counters.get(key) + 1;
//			counters.put(key, currentNum);
//			log.info("incr:" + key);
//			JedisTemplate writer = JedisTemplate.getWriterInstance();
//			String redisKey = "nginxLog:" + key;
//			writer.incr(redisKey);
//		}
	}

}
