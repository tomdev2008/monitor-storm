package com.lvtu.monitor.storm.bolts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.lvtu.monitor.storm.entity.NginxLog;
import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.jedis.JedisTemplate;

/**
 * @Title: ApiAccessCounter.java
 * @Package com.lvtu.monitor.storm.bolts
 * @Description: 统计每个Api接口的访问次数,并保存redis
 * @author lvzimin
 * @date 2015年5月15日 下午2:37:55
 * @version V1.0.0
 */
public class ApiAccessCounter extends BaseBasicBolt {

	private Log log = LogFactory.getLog(this.getClass());

	private static final long serialVersionUID = 656771398324679198L;

	private Integer taskId;

	private String componentName;

	private Map<String, Integer> accessCounters;

	private AtomicLong executeCount = new AtomicLong(0);

	/**
	 * storm clutter停止时执行
	 */
	@Override
	public void cleanup() {
		log.info("-- Api Access Counter [" + componentName + "-" + taskId + "] --");
		for (Map.Entry<String, Integer> entry : accessCounters.entrySet()) {
			log.info(entry.getKey() + ": " + entry.getValue());
		}
	}

	/**
	 * storm clutter开启时执行
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		this.accessCounters = new HashMap<String, Integer>();
		this.componentName = context.getThisComponentId();
		this.taskId = context.getThisTaskId();
	}

	/**
	 * 定义输出字段
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("accessCounterOutput"));
	}

	/**
	 * 当收到数据时执行
	 */
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		NginxLog logEntity = (NginxLog) input.getValueByField("ngxlog");
		if (logEntity == null) {
			return;
		}
		// 获取接口的方法名和版本号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(logEntity.getTimeLocal());
		String key = logEntity.getMethod() + "_" + logEntity.getVersion() + "_"
				+ dateStr;

		JedisTemplate writer = JedisTemplate.getWriterInstance();
		if (!accessCounters.containsKey(key)) {
			// 第一次统计到的接口,加入到redis的接口集合中
			writer.sadd(Constant.REDIS_KEY_API_SET + "_" + dateStr, key);
			accessCounters.put(key, 1);
		} else {
			Integer currentNum = accessCounters.get(key) + 1;
			accessCounters.put(key, currentNum);
		}
		log.info("incr:" + key);

		String redisKey = Constant.REDIS_KEY_LOG_PREFIX + key;
		writer.incr(redisKey);
		executeCount.incrementAndGet();
		if (executeCount.longValue() % 1000 == 0) {
			log.info("ApiAccessCounter has executed " + executeCount
					+ " tuples");
		}
		// collector.emit(new Values("test"));
	}

}
