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
 * @Description: 统计每个Ip的访问次数
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

	@Override
	public void cleanup() {
		log.info("-- Word Counter [" + componentName + "-" + taskId
				+ "] --");
		for (Map.Entry<String, Integer> entry : counters.entrySet()) {
			log.info(entry.getKey() + ": " + entry.getValue());
		}
	}

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
		
//		String str = (String)input.getValueByField("accessCounterOutput");
//		System.out.println(str);

		NginxLog logEntity = (NginxLog) input.getValueByField("ngxlog");
		
		if (logEntity == null) {
			return;
		}
		log.info("ip:" + logEntity.getRemoteAddr());
		String key = logEntity.getMethod() + "_" + logEntity.getVersion();
		if (!counters.containsKey(key)) {
			counters.put(key, 1);
		} else {
			Integer currentNum = counters.get(key) + 1;
			counters.put(key, currentNum);
			log.info("incr:" + key);
		}
	}

}
