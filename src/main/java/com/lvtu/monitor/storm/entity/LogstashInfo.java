package com.lvtu.monitor.storm.entity;

import java.io.Serializable;

/**
 * @Title: LogstashInfo.java
 * @Package com.lvtu.monitor.storm.entity
 * @Description: TODO
 * @author lvzimin
 * @date 2015年5月25日 下午2:59:23
 * @version V1.0.0
 */
public class LogstashInfo implements Serializable {

	private static final long serialVersionUID = -6156331676582089499L;

	private String message;

	private String host;

	private String path;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
