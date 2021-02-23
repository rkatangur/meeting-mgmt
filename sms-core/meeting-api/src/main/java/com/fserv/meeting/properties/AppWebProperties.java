package com.fserv.meeting.properties;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppWebProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppWebProperties.class);
	// set by spring at startup
	private static Properties APP_PROPERTIES = null;

	private static AppWebProperties INSTANCE = new AppWebProperties();

	private AppWebProperties() {
	}

	public static synchronized AppWebProperties getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new AppWebProperties();
			LOGGER.info("Created AppWebProperties instance.");
		}
		return AppWebProperties.INSTANCE;
	}

	public void setAppProperties(Properties app_properties) {
		APP_PROPERTIES = app_properties;
		LOGGER.info("Loaded AppWebProperties");
	}

	public Properties getAppProperties() {
		return APP_PROPERTIES;
	}

	public synchronized void setAppProperties(String appPropertiesPath) {
		try {
			if ((APP_PROPERTIES == null) || APP_PROPERTIES.size() == 0) {
				APP_PROPERTIES = new Properties();
				APP_PROPERTIES.load(new FileInputStream(appPropertiesPath));
			}

		} catch (Exception e) {
			LOGGER.error("Error in loading AppWebProperties from " + appPropertiesPath, e);
		}
		LOGGER.info("Loaded AppWebProperties from: " + appPropertiesPath);
	}

	public String getKey(String key) {
		return APP_PROPERTIES.get(key) != null ? (String) APP_PROPERTIES.get(key) : "0";
	}

	public String getProperty(String propName) {
		return APP_PROPERTIES.getProperty(propName);
	}

	public String getProperty(String stName, String stDefault) {
		String stReturn = APP_PROPERTIES.getProperty(stName);
		if (stReturn == null) {
			stReturn = stDefault;
		}
		return stReturn;
	}

}
