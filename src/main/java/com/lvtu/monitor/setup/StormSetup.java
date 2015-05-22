package com.lvtu.monitor.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import backtype.storm.LocalCluster;

import com.lvtu.monitor.job.ElasticIndexJob;
import com.lvtu.monitor.storm.topology.ApiStatTopology;
import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.elastic.ElasticWriter;
import com.lvtu.monitor.util.httpsqs4j.HttpsqsClientWrapper;

/**
 * @Title: StormSetup.java
 * @Package com.lvtu.monitor.setup
 * @Description: 应用启动和停止时执行
 * @author lvzimin
 * @date 2015年5月14日 下午12:44:02
 * @version V1.0.0
 */
@IocBean
public class StormSetup implements Setup {

	private Log log = LogFactory.getLog(this.getClass());

	private LocalCluster cluster;

	@Override
	public void init(NutConfig config) {

		log.info("stormSetup.init...");
		Ioc ioc = config.getIoc();
		// 初始化httpsqs队列客户端
		this.initHttpsqsClient(ioc);
		// 启动storm并加载任务
		this.startupCluster(ioc);
		// 初始化elastic-search客户端
		this.initElasticClient(ioc);
		// 启动更新elastic索引的定时任务
		this.startupElasticJob(ioc);
	}

	@Override
	public void destroy(NutConfig config) {
		log.info("stormSetup.destroy...");
		Ioc ioc = config.getIoc();
		// 停止Elastic
		this.shutdownElasticClient(ioc);
		// 停止storm
		this.shutdownCluster();
	}

	private void initHttpsqsClient(Ioc ioc) {
		HttpsqsClientWrapper clientWrapper = ioc
				.get(HttpsqsClientWrapper.class);
		clientWrapper.init();
	}

	private void initElasticClient(Ioc ioc) {
		ElasticWriter elasticWriter = ioc.get(ElasticWriter.class);
		elasticWriter.init();
	}

	private void shutdownElasticClient(Ioc ioc) {
		ElasticWriter elasticWriter = ioc.get(ElasticWriter.class);
		elasticWriter.close();
	}

	private void startupCluster(Ioc ioc) {

		cluster = new LocalCluster();
		ApiStatTopology apiStatTopology = ioc.get(ApiStatTopology.class);
		cluster.submitTopology(apiStatTopology.getName(),
				apiStatTopology.getConfig(), apiStatTopology.getTopology());
	}

	private void shutdownCluster() {
		cluster.shutdown();
	}

	private void startupElasticJob(Ioc ioc) {
		boolean isMaster = Boolean.valueOf(Constant.getValue("node.master"));
		// 只有主节点运行job
		if (isMaster) {
			ElasticIndexJob indexJob = ioc.get(ElasticIndexJob.class);
			indexJob.run();
		}
	}

}
