package com.lvtu.monitor.storm.bolts;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import com.lvtu.monitor.storm.entity.NginxLog;
import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.Constant.PROPERTY_FILE;
import com.lvtu.monitor.util.elastic.ElasticWriter;
import com.lvtu.monitor.util.jedis.JedisTemplate;

/**
 * @Title: AbnormalAccessRecorder.java
 * @Package com.lvtu.monitor.storm.bolts
 * @Description: 保存不正常的访问日志(速度慢,错误码)
 * @author lvzimin
 * @date 2015年5月25日 下午4:20:07
 * @version V1.0.0
 */
public class AbnormalAccessRecorder extends BaseBasicBolt {

	private static final long serialVersionUID = -2037089371354061741L;

	private Log log = LogFactory.getLog(this.getClass());

	private Integer taskId;

	private String componentName;

	/**
	 * 访问慢的数量
	 */
	private int slowCount = 0;

	/**
	 * 返回错误码的数量
	 */
	private int errorCount = 0;

	@Override
	public void cleanup() {
		log.info("-- Abnormal Access Recorder [" + componentName + "-" + taskId
				+ "] --");
		log.info("recorded " + slowCount + "slow access logs");
		log.info("recorded " + errorCount + "error access logs");
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		this.componentName = context.getThisComponentId();
		this.taskId = context.getThisTaskId();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		NginxLog logEntity = (NginxLog) input.getValueByField("ngxlog");

		if (logEntity == null) {
			return;
		}

		String index = Constant.getValue("ngxlog.index", PROPERTY_FILE.ELASTIC);

		// Map<String, String> objMap = new HashMap<String, String>();
		// objMap.put("method", logEntity.getMethod());
		// objMap.put("version", logEntity.getVersion());

		if (logEntity.getStatus() != HttpStatus.SC_OK) {
			errorCount++;
			ElasticWriter.write(index, "error_log", UUID.randomUUID()
					.toString(), logEntity);
		}

		float slowAccessSecond = Float.valueOf(Constant
				.getValue("slow_access_second"));
		if (logEntity.getRequestTime() > slowAccessSecond) {
			slowCount++;
			ElasticWriter.write(index, "slow_log", UUID.randomUUID()
					.toString(), logEntity);

		}
	}

}
