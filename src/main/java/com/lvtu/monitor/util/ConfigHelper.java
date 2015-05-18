package com.lvtu.monitor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

	/**
	 * 根据路径返回配置文件
	 * 
	 * @param path
	 * @return
	 */
	public static final Properties getProperties(String path) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = ConfigHelper.class.getClassLoader().getResourceAsStream(path);
			properties.load(inputStream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}