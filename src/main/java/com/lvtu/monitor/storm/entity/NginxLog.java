package com.lvtu.monitor.storm.entity;

import java.io.Serializable;
import java.util.Map;

public class NginxLog implements Serializable {

	private static final long serialVersionUID = -7506569473260140787L;

	private String addr;
	
	private String uri;
	
	private String agent;
	
	private String time;
	
	private Map<String, Object> args;
	
	private String method;
	
	private String version;

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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
