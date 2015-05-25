package com.lvtu.monitor.storm.entity;

import java.util.Map;

/**
 * @Title: NginxRequestInfo.java
 * @Package com.lvtu.monitor.storm.entity
 * @Description: TODO
 * @author lvzimin
 * @date 2015年5月25日 上午10:15:41
 * @version V1.0.0
 */
public class NginxRequestInfo {

	/**
	 * http方法(GET,POST)
	 */
	private String httpMethod;

	/**
	 * 请求uri
	 */
	private String requestUri;

	/**
	 * 请求参数
	 */
	private Map<String, Object> requestParams;

	/**
	 * http协议(HTTP/1.1)
	 */
	private String httpProtocol;

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public Map<String, Object> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String, Object> requestParams) {
		this.requestParams = requestParams;
	}

	public String getHttpProtocol() {
		return httpProtocol;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

	@Override
	public String toString() {
		return "NginxRequestInfo [httpMethod=" + httpMethod + ", requestUri="
				+ requestUri + ", requestParams=" + requestParams
				+ ", httpProtocol=" + httpProtocol + "]";
	}

}
