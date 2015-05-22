package com.lvtu.monitor.storm.spouts;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nutz.json.Json;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.lvtu.monitor.storm.entity.NginxLog;
import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.Constant.PROPERTY_FILE;
import com.lvtu.monitor.util.httpsqs4j.HttpsqsClient;
import com.lvtu.monitor.util.httpsqs4j.HttpsqsClientWrapper;
import com.lvtu.monitor.util.httpsqs4j.HttpsqsException;

/** 
* @Title: NginxLogReader.java 
* @Package com.lvtu.monitor.storm.spouts 
* @Description: 读取httpsqs中的日志消息
* @author lvzimin 
* @date 2015年5月19日 上午11:15:35 
* @version V1.0.0 
*/
public class NginxLogReader extends BaseRichSpout {

	private static final long serialVersionUID = 13975009225232753L;
	
	private Log log = LogFactory.getLog(this.getClass());

	private SpoutOutputCollector collector;
	
	private AtomicLong emitCount = new AtomicLong(0);

	/**
	 * 提交的数据被处理成功时执行
	 */
	public void ack(Object msgId) {
		log.info("OK:"+msgId);
	}

	/**
	 * storm cluster关闭时执行
	 */
	public void close() {

	}

	/**
	 * 提交的数据被处理失败时执行
	 */
	public void fail(Object msgId) {
		log.info("FAIL:" + msgId);
	}

	/**
	 * 提交的数据被处理失败时执行
	 */
	public void nextTuple() {

		HttpsqsClient client = HttpsqsClientWrapper.getClient();
		String queueName = Constant.getValue("queue.ngxlog", PROPERTY_FILE.HTTPSQS);
		try {
			String result = client.getString(queueName);
			log.info(result);
			NginxLog nginxLog = Json.fromJson(NginxLog.class, result);
			this.collector.emit(new Values(nginxLog), "nginxLog");
		} catch (HttpsqsException e) {
			
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			log.info("no message in queue now, sleep for 10s");
		}
		emitCount.incrementAndGet();
		if (emitCount.longValue() % 1000 == 0) {
			log.info("NginxLogReader has emitted " + emitCount + " tuples");
		}
	}

	/**
	 * storm cluster开启时执行
	 */
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
	}

	/**
	 * 定义输出字段
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("ngxlog"));
	}
}
