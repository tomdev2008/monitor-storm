package com.lvtu.monitor.util;

import java.util.Properties;

/** 
* @Title: Constant.java 
* @Package com.lvtu.monitor.util 
* @Description: 定义常量和读取配置文件 
* @author lvzimin 
* @date 2015年5月18日 下午3:12:25 
* @version V1.0.0 
*/
public class Constant {

	/**
	 * redis中存储nginx日志的key前缀
	 */
	public static final String REDIS_KEY_LOG_PREFIX = "nginxLog:"; 
	
	/**
	 * redis中用set存储所有接口的key
	 */
	public static final String REDIS_KEY_API_SET = REDIS_KEY_LOG_PREFIX + "api_set";

	public static String getValue(String key) {
		return getValue(key, PROPERTY_FILE.CONST);
	}

	public static String getValue(String key, PROPERTY_FILE propertyFile) {
		Properties properties = ConfigHelper.getProperties(propertyFile.getFileName());
		return properties.getProperty(key);
	}
	
	public enum PROPERTY_FILE {
		
		CONST("const.properties"),
		ELASTIC("elastic.properties"),
		HTTPSQS("httpsqs.properties"),
		REDIS("redis.properties");
		
		private String fileName;
		
		PROPERTY_FILE(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return fileName;
		}
	}

}
