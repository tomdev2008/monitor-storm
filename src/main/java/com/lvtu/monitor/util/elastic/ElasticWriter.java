package com.lvtu.monitor.util.elastic;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;

import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.Constant.PROPERTY_FILE;

/**
 * @Title: ElasticWriter.java
 * @Package com.lvtu.monitor.util.elastic
 * @Description: elastic-search的索引写入工具类
 * @author lvzimin
 * @date 2015年5月15日 下午4:35:34
 * @version V1.0.0
 */
@IocBean(singleton = true)
public class ElasticWriter {

	private static Client elasticClient;

	public void init() {
		String clusterName = Constant.getValue("elastic.cluster.name",
				PROPERTY_FILE.ELASTIC);
		String elasticIp = Constant.getValue("elastic.ip",
				PROPERTY_FILE.ELASTIC);
		Integer elasticPort = Integer.valueOf(Constant.getValue("elastic.port",
				PROPERTY_FILE.ELASTIC));
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName).build();
		elasticClient = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(elasticIp,
						elasticPort));
	}

	public void close() {
		elasticClient.close();
	}

	/**
	 * 将对象保存到索引
	 * 
	 * @param index 索引名称
	 * @param type	索引类型
	 * @param id	索引ID
	 * @param obj	保存的对象
	 */
	public static void write(String index, String type, String id, Object obj) {

		String json = Json.toJson(obj);

		IndexRequest indexRequest = new IndexRequest(index, type, id)
				.source(json);
		try {
			// id不存在,新建;id已存在,更新
			elasticClient.index(indexRequest).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
