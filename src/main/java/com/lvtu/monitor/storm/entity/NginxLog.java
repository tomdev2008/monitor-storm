package com.lvtu.monitor.storm.entity;

import java.io.Serializable;
import java.util.Map;

/** 
* @Title: NginxLog.java 
* @Package com.lvtu.monitor.storm.entity 
* @Description: 对应httpsqs队列中取出的json格式nginx日志 
* @author lvzimin 
* @date 2015年5月19日 上午11:09:44 
* @version V1.0.0 
*/
public class NginxLog implements Serializable {

	private static final long serialVersionUID = -7506569473260140787L;

	/**
	 * ip地址
	 */
	private String addr;
	
	/**
	 * 访问uri
	 */
	private String uri;
	
	/**
	 * userAgent
	 */
	private String agent;
	
	/**
	 * 请求时间
	 */
	private String time;
	
	/**
	 * 额外参数
	 */
	private Map<String, Object> args;
	
	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getTime() {
		// TODO
		// 按日期统计log,日期格式
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}

	/**
	 * 接口方法
	 * @return
	 */
	public String getMethod() {
		if (isHessianRequest()) {
			return this.uri;
		} else if (this.getArgs() != null) {
			String method = String.valueOf(this.args.get("method"));
			if (method.contains(",")) {
				method = method.substring(0, method.indexOf(","));
			}
			return method;
		}
		return null;
	}

	/**
	 * 接口版本
	 * @return
	 */
	public String getVersion() {
		if (isHessianRequest()) {
			return null;
		} else if (this.getArgs() != null) {
			String version = String.valueOf(this.args.get("version"));
			if (version.contains(",")) {
				version = version.substring(0, version.indexOf(","));
			}
			return version;
		}
		return null;
	}

	/**
	 * 是否是hessian请求
	 * @return
	 */
	private boolean isHessianRequest() {
		return !agent.isEmpty() && agent.startsWith("Java");
	}
	
}
