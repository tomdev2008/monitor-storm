package com.lvtu.monitor.storm.spouts;

import java.util.Map;

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

public class NginxLogReader extends BaseRichSpout {

	private static final long serialVersionUID = 13975009225232753L;
	
	private Log log = LogFactory.getLog(this.getClass());

	private SpoutOutputCollector collector;

	public void ack(Object msgId) {
		// log.info("OK:"+msgId);
	}

	public void close() {

	}

	public void fail(Object msgId) {
		log.info("FAIL:" + msgId);
	}

	public void nextTuple() {

		HttpsqsClient client = HttpsqsClientWrapper.getClient();
		// TODO 读配置文件
		String queueName = Constant.getValue("queue.ngxlog", PROPERTY_FILE.HTTPSQS);
		try {
			String result = client.getString(queueName);
			log.info(result);
			NginxLog nginxLog = Json.fromJson(NginxLog.class, result);
			if (nginxLog.getArgs() != null) {
				nginxLog.setMethod(String.valueOf(nginxLog.getArgs().get("method")));
				nginxLog.setVersion(String.valueOf(nginxLog.getArgs().get("version")));
			}
			this.collector.emit(new Values(nginxLog), "nginxLog");
		} catch (HttpsqsException e) {
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log"));
	}
}
