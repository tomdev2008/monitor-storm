package com.lvtu.monitor.storm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title: NginxLog.java
 * @Package com.lvtu.monitor.storm.entity
 * @Description: 对应httpsqs队列中取出的nginx日志
 * @author lvzimin
 * @date 2015年5月19日 上午11:09:44
 * @version V1.0.0
 */
public class NginxLog implements Serializable {

	private static final long serialVersionUID = -7506569473260140787L;

	/**
	 * 客户端IP地址
	 */
	private String remoteAddr;

	/**
	 * 客户端用户名称
	 */
	private String remoteUser;

	/**
	 * 通用日志格式下的本地时间
	 */
	private Date timeLocal;

	/**
	 * 客户端请求的域名
	 */
	private String httpHost;

	/**
	 * 记录请求的URL和HTTP协议
	 */
	private NginxRequestInfo request;

	/**
	 * 状态码
	 */
	private int status;

	/**
	 * 发送给客户端的总字节数
	 */
	private int bodyBytesSent;

	/**
	 * http请求端的真正IP
	 */
	private String httpXforwardedFor;

	/**
	 * 客户端浏览器相关信息
	 */
	private String httpUserAgent;
	
	/**
	 * 请求响应时间
	 */
	private float requestTime;

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public Date getTimeLocal() {
		return timeLocal;
	}

	public void setTimeLocal(Date timeLocal) {
		this.timeLocal = timeLocal;
	}

	public String getHttpHost() {
		return httpHost;
	}

	public void setHttpHost(String httpHost) {
		this.httpHost = httpHost;
	}

	public NginxRequestInfo getRequest() {
		return request;
	}

	public void setRequest(NginxRequestInfo request) {
		this.request = request;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getBodyBytesSent() {
		return bodyBytesSent;
	}

	public void setBodyBytesSent(int bodyBytesSent) {
		this.bodyBytesSent = bodyBytesSent;
	}

	public String getHttpXforwardedFor() {
		return httpXforwardedFor;
	}

	public void setHttpXforwardedFor(String httpXforwardedFor) {
		this.httpXforwardedFor = httpXforwardedFor;
	}

	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	public float getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(float requestTime) {
		this.requestTime = requestTime;
	}

	/**
	 * 接口方法
	 * 
	 * @return
	 */
	public String getMethod() {
		if (isHessianRequest()) {
			return this.request.getRequestUri();
		} else if (this.request.getRequestParams() != null) {
			String method = String.valueOf(this.request.getRequestParams().get(
					"method"));
			if (method.contains(",")) {
				method = method.substring(0, method.indexOf(","));
			}
			return method;
		}
		return null;
	}

	/**
	 * 接口版本
	 * 
	 * @return
	 */
	public String getVersion() {
		if (isHessianRequest()) {
			return null;
		} else if (this.request.getRequestParams() != null) {
			String version = String.valueOf(this.request.getRequestParams()
					.get("version"));
			if (version.contains(",")) {
				version = version.substring(0, version.indexOf(","));
			}
			return version;
		}
		return null;
	}

	/**
	 * 是否是hessian请求
	 * 
	 * @return
	 */
	private boolean isHessianRequest() {
		return !httpUserAgent.isEmpty() && httpUserAgent.startsWith("Java");
	}

	@Override
	public String toString() {
		return "NginxLog [remoteAddr=" + remoteAddr + ", remoteUser="
				+ remoteUser + ", timeLocal=" + timeLocal + ", httpHost="
				+ httpHost + ", request=" + request + ", status=" + status
				+ ", bodyBytesSent=" + bodyBytesSent + ", httpXforwardedFor="
				+ httpXforwardedFor + ", httpUserAgent=" + httpUserAgent
				+ ", requestTime=" + requestTime + ",method="
				+ getMethod() + ", version=" + getVersion() + "]";
	}

}
