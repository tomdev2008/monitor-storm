package com.lvtu.monitor.util.httpsqs4j;

import org.nutz.ioc.loader.annotation.IocBean;

import com.lvtu.monitor.util.Constant;
import com.lvtu.monitor.util.Constant.PROPERTY_FILE;

/** 
 * @Title: HttpsqsClientWrapper.java 
 * @Package com.lvtu.monitor.util.httpsqs4j 
 * @Description: HttpsqsClient的单例包装类 
 * @author lvzimin 
 * @date 2015年5月14日 下午2:47:37 
 * @version V1.0.0 
 */
@IocBean(singleton = true)
public class HttpsqsClientWrapper {

	private static HttpsqsClient client;
	
	public static HttpsqsClient getClient() {
		return client;
	}
	
	public void init() {
		String ip = Constant.getValue("httpsqs.ip", PROPERTY_FILE.HTTPSQS);
		int port = Integer.valueOf(Constant.getValue("httpsqs.port", PROPERTY_FILE.HTTPSQS));
		String charset = Constant.getValue("httpsqs.charset", PROPERTY_FILE.HTTPSQS);
		try {
			Httpsqs4j.setConnectionInfo(ip, port, charset);
			HttpsqsClient newClient = Httpsqs4j.createNewClient();
			client = newClient;
		} catch (HttpsqsException e) {
			e.printStackTrace();
		}
	}
	
}
