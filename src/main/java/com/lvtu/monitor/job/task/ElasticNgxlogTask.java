package com.lvtu.monitor.job.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.Constant.PROPERTY_FILE;
import com.lvtu.monitor.util.elastic.ElasticWriter;
import com.lvtu.monitor.util.jedis.JedisTemplate;

/**
 * @Title: ElasticNgxlogTask.java
 * @Package com.lvtu.monitor.job.task
 * @Description: 更新ngxlog统计在elastic中的索引
 * @author lvzimin
 * @date 2015年5月18日 下午3:31:03
 * @version V1.0.0
 */
public class ElasticNgxlogTask implements Runnable {

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void run() {
		Set<String> apiSet = this.getApiSet();
		log.info("find " + apiSet.size() + " apis");
		for (String apiInfo : apiSet) {
			String[] methodAndVersion = apiInfo.split("_");
			if (methodAndVersion.length == 3) {
				String method = methodAndVersion[0];
				String version = methodAndVersion[1];
				String date = methodAndVersion[2];
				String count = this.getApiAccessCount(apiInfo);
				this.write2Elastic(method, version, date, count);
			}
		}
	}

	/**
	 * 查询所有统计的api接口
	 * 
	 * @return
	 */
	private Set<String> getApiSet() {
		JedisTemplate reader = JedisTemplate.getReaderInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(new Date());
		Set<String> apiSet = reader.smembers(Constant.REDIS_KEY_API_SET + "_"
				+ dateStr);
		return apiSet;
	}

	/**
	 * 查询指定接口的访问数量
	 * 
	 * @param apiInfo
	 * @return
	 */
	private String getApiAccessCount(String apiInfo) {
		JedisTemplate reader = JedisTemplate.getReaderInstance();
		String accessCount = reader.get(Constant.REDIS_KEY_LOG_PREFIX + apiInfo);
		return accessCount;
	}

	/**
	 * 更新elastic的索引
	 * 
	 * @param method
	 *            接口名称
	 * @param version
	 *            接口版本
	 * @param count
	 *            访问数量
	 */
	private void write2Elastic(String method, String version, String date, String count) {
		log.info("write2Elastic:" + method + "|" + version + "|" + count);
		String id = method + "_" + version + "_" + date;
		String index = Constant.getValue("ngxlog.index", PROPERTY_FILE.ELASTIC);
		Map<String, String> objMap = new HashMap<String, String>();
		objMap.put("count", count);
		objMap.put("method", method);
		objMap.put("version", version);
		objMap.put("date", date);
		ElasticWriter.write(index, "ngx_log", id, objMap);
	}

}
