package com.lvtu.monitor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lvtu.monitor.storm.entity.NginxLog;
import com.lvtu.monitor.storm.entity.NginxRequestInfo;

/**
 * @Title: NginxLogConvertor.java
 * @Package com.lvtu.monitor.util
 * @Description: 将nginx的日志转换成对象
 * @author lvzimin
 * @date 2015年5月25日 上午10:27:03
 * @version V1.0.0
 */
public class NginxLogConvertor {

	private static Log logger = LogFactory.getLog(NginxLogConvertor.class);

	// 192.168.30.133 - - [24/Apr/2015:05:07:41 +0800] "api3g2.lvmama.com"
	// "GET /api/router/rest.do?method=api.com.groupbuy.getGroupbuyOrSecKillDetail&version=1.0.0&firstChannel=AI&secondChannel=aiactivity&lvversion=6.0.0&suppGoodsId=1691051&branchType=BRANCH HTTP/1.1"
	// 200 3750 "-" "Apache-HttpClient/4.2.1 (java 1.5)" "-" "-" "-" "-"

	public static NginxLog convert(String logContent) {
		logContent = logContent.replaceAll("\" ", "###")
				.replaceAll(" \"", "###").replaceAll("\"", "");
		System.out.println(logContent);
		String[] params = logContent.split("###");
		NginxLog logEntity = new NginxLog();
		if (params.length < 11) {
			logger.error("params not enough...find " + params.length + ",log:["
					+ logContent + "]");
			return null;
		}
		String ipAndDate = params[0];
		String host = params[1];
		String request = params[2];
		String statusAndBytes = params[3];
		String xForwardedFor = params[4];
		String userAgent = params[5];
		String requestTime = params[10];

		logEntity.setHttpHost(host);
		logEntity.setHttpXforwardedFor(xForwardedFor);
		logEntity.setHttpUserAgent(userAgent);
		logEntity.setRequestTime(Float.valueOf(requestTime));

		String[] ipDateArr = ipAndDate.split(" ");
		if (ipDateArr.length == 5) {
			String ip = ipDateArr[0];
			String dateStr = ipDateArr[3].replace("[", "");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss",
					Locale.ENGLISH);
			Date date = null;
			try {
				date = sdf.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			logEntity.setRemoteAddr(ip);
			logEntity.setTimeLocal(date);
		}

		String[] statusBytesArr = statusAndBytes.split(" ");
		if (statusBytesArr.length == 2) {
			String status = statusBytesArr[0];
			String bytes = statusBytesArr[1];

			logEntity.setStatus(Integer.valueOf(status));
			logEntity.setBodyBytesSent(Integer.valueOf(bytes));
		}

		String[] requestArr = request.split(" ");
		if (requestArr.length == 3) {
			String httpMethod = requestArr[0];
			String url = requestArr[1];
			String protocol = requestArr[2];

			NginxRequestInfo requestInfo = new NginxRequestInfo();
			requestInfo.setHttpMethod(httpMethod);
			requestInfo.setHttpProtocol(protocol);

			if (url.contains("?")) {
				String[] uriAndParam = url.split("\\?");
				if (uriAndParam.length == 2) {
					String uri = uriAndParam[0];
					String paramStr = uriAndParam[1];
					requestInfo.setRequestUri(uri);
					System.out.println(paramStr);

					Map<String, Object> requestParams = new HashMap<String, Object>();

					String[] requestParamArr = paramStr.split("&");
					for (String requestParam : requestParamArr) {
						if (requestParam.contains("=")) {
							String[] nameAndValue = requestParam.split("=");
							if (nameAndValue.length == 2) {
								String paramName = nameAndValue[0];
								String paramValue = nameAndValue[1];
								if (requestParams.get(paramName) == null) {
									requestParams.put(paramName, paramValue);
								} else {
									requestParams.put(paramName,
											requestParams.get(paramName) + ","
													+ paramValue);
								}
							}
						}
					}

					requestInfo.setRequestParams(requestParams);
				}
			} else {
				requestInfo.setRequestUri(url);
				requestInfo.setRequestParams(new HashMap<String, Object>());
			}
			logEntity.setRequest(requestInfo);
		}
		return logEntity;
	}

	public static NginxLog testConvert(String logContent) {
		logContent = logContent.replaceAll("\" ", "###")
				.replaceAll(" \"", "###").replaceAll("\"", "")
				.replaceAll("  ", " ");
		System.out.println(logContent);
		String[] params = logContent.split("###");
		NginxLog logEntity = new NginxLog();
		if (params.length < 7) {
			logger.error("params not enough...find " + params.length + ",log:["
					+ logContent + "]");
			return null;
		}
		String ipAndDate = params[0];
		String request = params[1];
		String statusAndBytes = params[2];
		String xForwardedFor = params[3];
		String userAgent = params[4];
		String requestTime = params[6];

		logEntity.setHttpXforwardedFor(xForwardedFor);
		logEntity.setHttpUserAgent(userAgent);
		logEntity.setRequestTime(Float.valueOf(requestTime));

		String[] ipDateArr = ipAndDate.split(" ");
		if (ipDateArr.length == 5) {
			String ip = ipDateArr[0];
			String dateStr = ipDateArr[3].replace("[", "");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss",
					Locale.ENGLISH);
			Date date = null;
			try {
				date = sdf.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			logEntity.setRemoteAddr(ip);
			logEntity.setTimeLocal(date);
		}

		String[] statusBytesArr = statusAndBytes.split(" ");
		if (statusBytesArr.length == 2) {
			String status = statusBytesArr[0];
			String bytes = statusBytesArr[1];

			logEntity.setStatus(Integer.valueOf(status));
			logEntity.setBodyBytesSent(Integer.valueOf(bytes));
		}

		String[] requestArr = request.split(" ");
		if (requestArr.length == 3) {
			String httpMethod = requestArr[0];
			String url = requestArr[1];
			String protocol = requestArr[2];

			NginxRequestInfo requestInfo = new NginxRequestInfo();
			requestInfo.setHttpMethod(httpMethod);
			requestInfo.setHttpProtocol(protocol);
			requestInfo.setRequestParams(new HashMap<String, Object>());

			if (url.contains("?")) {
				String[] uriAndParam = url.split("\\?");
				if (uriAndParam.length == 2) {
					String uri = uriAndParam[0];
					String paramStr = uriAndParam[1];
					requestInfo.setRequestUri(uri);
					System.out.println(paramStr);

					Map<String, Object> requestParams = new HashMap<String, Object>();

					String[] requestParamArr = paramStr.split("&");
					for (String requestParam : requestParamArr) {
						if (requestParam.contains("=")) {
							String[] nameAndValue = requestParam.split("=");
							if (nameAndValue.length == 2) {
								String paramName = nameAndValue[0];
								String paramValue = nameAndValue[1];
								if (requestParams.get(paramName) == null) {
									requestParams.put(paramName, paramValue);
								} else {
									requestParams.put(paramName,
											requestParams.get(paramName) + ","
													+ paramValue);
								}
							}
						}
					}

					requestInfo.setRequestParams(requestParams);
				}
			} else {
				requestInfo.setRequestUri(url);
			}
			logEntity.setRequest(requestInfo);
		}
		return logEntity;
	}

	public static void main(String[] args) throws ParseException {

		// String [] logs = new
		// String[]{"192.168.30.133 - - [24/Apr/2015:05:07:40 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.seckill.getSeckillStatus&version=1.0.0&firstChannel=AI&secondChannel=aiactivity&lvversion=6.0.0&suppGoodsId=1691051&branchType=BRANCH HTTP/1.1\" 200 187 \"-\" \"Apache-HttpClient/4.2.1 (java 1.5)\" \"-\" \"-\" \"-\" \"-\" ","117.136.45.154 - - [24/Apr/2015:05:07:40 +0800] \"api3g2.lvmama.com\" \"POST /api/router/rest.do?method=api.com.ticket.search.searchTicket&formate=json&version=1.0.0&lvsessionid=1cf7756a-a9a9-4552-ab49-f58f689df6be&udid=357457046489465&firstChannel=ANDROID&secondChannel=HUAWEI&lvversion=6.3.2&osVersion=4.4.2&deviceName=HUAWEI+P7-L07&globalLongitude=118.773163&globalLatitude=32.055103 HTTP/1.1\" 200 31019 \"-\" \"ANDROID_HUAWEI LVMM/6.3.2 (HUAWEI P7-L07; Android OS 4.4.2; 46000)\" \"-\" \"-\" \"-\" \"-\" ","192.168.10.205 - - [24/Apr/2015:05:07:40 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.route.product.getRouteProductNearestGroupDate&version=1.0.0&productId=221017&firstChannel=PHP&secondChannel=cms HTTP/1.1\" 200 93 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)\" \"-\" \"-\" \"-\" \"-\" ","192.168.10.205 - - [24/Apr/2015:05:07:40 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.route.search.searchRoute&version=1.0.0&keyword=219235&firstChannel=PHP&secondChannel=cms HTTP/1.1\" 200 225 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)\" \"-\" \"-\" \"-\" \"-\" ","192.168.10.205 - - [24/Apr/2015:05:07:41 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.route.product.getRouteProductNearestGroupDate&version=1.0.0&productId=219235&firstChannel=PHP&secondChannel=cms HTTP/1.1\" 200 93 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)\" \"-\" \"-\" \"-\" \"-\" ","192.168.10.205 - - [24/Apr/2015:05:07:41 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.route.search.searchRoute&version=1.0.0&keyword=199535&firstChannel=PHP&secondChannel=cms HTTP/1.1\" 200 2000 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)\" \"-\" \"-\" \"-\" \"-\" ","61.171.124.139 - - [24/Apr/2015:05:07:41 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.init.config&udid=352246064893061&osVersion=4.4.2&lvversion=5.2.1&formate=json&secondChannel=HIAPK&firstChannel=ANDROID HTTP/1.1\" 200 175 \"-\" \"ANDROID_HIAPK LVMM/5.2.1 (HTC D816w; Android OS 4.4.2; 46001)\" \"-\" \"-\" \"-\" \"-\" ","192.168.30.133 - - [24/Apr/2015:05:07:41 +0800] \"api3g2.lvmama.com\" \"GET /api/router/rest.do?method=api.com.groupbuy.getGroupbuyOrSecKillDetail&version=1.0.0&firstChannel=AI&secondChannel=aiactivity&lvversion=6.0.0&suppGoodsId=1691051&branchType=BRANCH HTTP/1.1\" 200 3750 \"-\" \"Apache-HttpClient/4.2.1 (java 1.5)\" \"-\" \"-\" \"-\" \"-\""};
		//
		// for (String log : logs) {
		// NginxLog logEntity = NginxLogConvertor.convert(log);
		// System.out.println(logEntity);
		// }

		String[] testLogs = new String[] { "192.168.0.227 - - [25/May/2015:15:49:12 +0800] \"GET /api/router/rest.do?method=api.com.ticket.search.searchTicket&version=1.0.0&keyword=172350&firstChannel=PHP&secondChannel=cms HTTP/1.1\" 200 224 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)\" \"-\" 0.068" };

		for (String log : testLogs) {
			NginxLog logEntity = NginxLogConvertor.testConvert(log);
			System.out.println(logEntity);
		}
		// String regex =
		// "(\\d+\\.\\d+\\.\\d+\\.\\d+) - - \\[(\\d+\\/\\w+\\/\\d+:\\d+:\\d+:\\d+) \\+\\d+] \\\"(api3g2.lvmama.com)\\\"";

		// String regex = "(\\d+\\/\\w+\\/\\d+:\\d+:\\d+:\\d+)";
		// Pattern pattern = Pattern.compile(regex);
		// Matcher matcher = pattern.matcher(log);
		//
		// while (matcher.find()) {
		// System.out.println(matcher.group(1));
		// System.out.println(matcher.group(2));
		// System.out.println(matcher.group(3));
		// }
	}

}
