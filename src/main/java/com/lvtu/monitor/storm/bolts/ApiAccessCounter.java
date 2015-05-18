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
import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.jedis.JedisTemplate;

/**
 * @Title: ApiAccessCounter.java
 * @Package com.lvtu.monitor.storm.bolts
 * @Description: TODO
 * @author lvzimin
 * @date 2015年5月15日 下午2:37:55
 * @version V1.0.0
 */
public class ApiAccessCounter extends BaseBasicBolt {
	
	private Log log = LogFactory.getLog(this.getClass());

	private static final long serialVersionUID = 656771398324679198L;

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
		// 获取接口的方法名和版本号
		String key = logEntity.getMethod() + "_" + logEntity.getVersion();
		
		JedisTemplate writer = JedisTemplate.getWriterInstance();
		if (!counters.containsKey(key)) {
			// 向
			writer.sadd(Constant.REDIS_KEY_API_SET, key);
			counters.put(key, 1);
		} else {
			Integer currentNum = counters.get(key) + 1;
			counters.put(key, currentNum);
		}
		log.info("incr:" + key);
		
		String redisKey = Constant.REDIS_KEY_LOG_PREFIX + key;
		writer.incr(redisKey);
	}

}
