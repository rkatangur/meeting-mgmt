package com.fserv.meeting.properties;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class EnvironmentProperties {

	private final static String APP_ENVIRONMENT_NAME = "app.env.name";
	private final static String APP_PROPERTY_FILE_NAME = "app.property.filename";
	private static Logger LOGGER = Logger.getLogger(EnvironmentProperties.class);

	private static NetService NET_SERVICE;

	private Map<String, String> hostNamePatternEnvMap;
	private Set<String> developerHostnamePatternEnvironmentSet;
	private String environmentName;
	private String environmentKey;
	private Map<String, String> environmentVariableMap;

	public Resource getOverrideResourceLocation() {
		String forcedName = getForcedName(APP_PROPERTY_FILE_NAME);
		if (forcedName == null || forcedName.isEmpty()) {
			return null;
		}
		LOGGER.info("Override Resource path:[" + forcedName + "]");
		File file = new File(forcedName);
		if (file.exists()) {
			return new FileSystemResource(file);
		} else {
			LOGGER.info("Override Resource path:[" + forcedName + "] does not exist.");
			return null;
		}
	}

	public Resource getResourceLocation(String locationFormat) {
		return getResourceLocation(locationFormat, false);
	}

	public Resource getResourceLocation(String locationFormat, boolean setSystem) {
		Resource location = null;
		String hostName = NET_SERVICE.getHostName();
		LOGGER.info("Host name:[" + hostName + "]");
		environmentName = getEnvironmentName(hostName);
		if (System.getProperty(APP_ENVIRONMENT_NAME) == null
				|| !System.getProperty(APP_ENVIRONMENT_NAME).equals(environmentName)) {
			System.setProperty(APP_ENVIRONMENT_NAME, environmentName);
		}
		LOGGER.info("Environment:[" + environmentName + "]. To override set env variable " + APP_ENVIRONMENT_NAME);
		String environmentKeyValue = getEnvironmentKeyValue();
		LOGGER.info("getResourceLocation : " + locationFormat + ": :" + environmentName + ": :" + environmentKeyValue);
		String resourcePath = String.format(locationFormat, environmentName, environmentKeyValue);
		LOGGER.info("getResourceLocation: Resource path:[" + resourcePath + "]");
		location = new ClassPathResource(resourcePath);

		// Test this properties file
		Properties p = new Properties();
		try {
			p.load(location.getInputStream());
			for (Object key : p.keySet()) {
				if (key instanceof String) {
					String keyStr = (String) key;
					String val = p.getProperty(keyStr);
					LOGGER.info(" system = " + setSystem + ", key = " + key + ", val = " + val);
					if (setSystem) {
						System.setProperty(keyStr, val);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Ignored. reason: " + e.getMessage());
		}
		return location;
	}

	public Resource getHostlessResourceLocation(String locationFormat) {
		Resource location;
		String environmentKeyValue = getEnvironmentKeyValue();
		String resourcePath = null;
		LOGGER.info(
				"In getHostlessResourceLocation: " + locationFormat + " environmentKeyValue: " + environmentKeyValue);
		if (environmentKeyValue == null) {
			LOGGER.warn("environmentKeyValue is null. it's unlikely that this will go well.");
		}
		try {
			resourcePath = String.format(locationFormat, environmentKeyValue);
		} catch (Exception e) {
			if (environmentKeyValue != null)
				LOGGER.info("getHostlessResourceLocation, failed with format: " + locationFormat + " keyval: "
						+ environmentKeyValue);
			return null;
		}
		LOGGER.info("Resource path [" + resourcePath + "] (hostless)");
		location = new ClassPathResource(resourcePath);

		// Test this properties file
		Properties p = new Properties();
		try {
			p.load(location.getInputStream());
			for (Object key : p.keySet()) {
				if (key instanceof String) {
					String keyStr = (String) key;
					String val = p.getProperty(keyStr);
					LOGGER.info("key = " + key + ", val = " + val);
				}
			}
		} catch (Exception e) {
			LOGGER.info("	ignored. reason:" + e.getMessage());
		}
		return location;
	}

	/**
	 * Marking as package for executing test cases....
	 * 
	 * @return
	 */

	private String getEnvironmentKeyValue() {
		if (environmentKey == null)
			return null;

		String envvarvalue = getForcedName(environmentKey);
		if (envvarvalue == null)
			return null;

		String bestKey = null;
		int maxLen = 0;

		for (String key : environmentVariableMap.keySet()) {
			if (envvarvalue.matches(key)) {
				final int len = key.length();
				if (len > maxLen) {
					bestKey = key;
					maxLen = len;
				}
			}
		}

		if (bestKey != null) {
			String result = environmentVariableMap.get(bestKey);
			LOGGER.info("Environment variable (" + bestKey + ") matches, result (" + result + ")");
			return result;
		}

		return null;
	}

	/**
	 * Get the environment name to perpend to the property file
	 * 
	 * @return string to prepend or empty string if unknow.
	 */
	private String getEnvironmentName(String hostName) {
		String result = "";
		// Try environment variable.
		String forcedEnvName = getForcedName(APP_ENVIRONMENT_NAME);

		if (forcedEnvName != null) {
			result = forcedEnvName;
			LOGGER.info("Using environment setting " + APP_ENVIRONMENT_NAME + "=" + result);
		} else {
			boolean hostnameFound = false;
			for (String key : hostNamePatternEnvMap.keySet()) {
				if (hostName.matches(key)) {
					result = hostNamePatternEnvMap.get(key);

					hostnameFound = true;
					LOGGER.info("Hostname matches regex:[" + key + "]");
					break;
				}
			}
			if (!hostnameFound) {
				for (String developerHosts : developerHostnamePatternEnvironmentSet) {
					if (hostName.matches(developerHosts)) {
						StringTokenizer tokenizer = new StringTokenizer(hostName, "-");
						tokenizer.nextToken();
						tokenizer.nextToken();
						result = tokenizer.nextToken();

						hostnameFound = true;
						LOGGER.info("Dev Hostname found:[" + result + "]");
						break;
					}
				}
			}
			if (!hostnameFound) {
				StringTokenizer tokenizer = new StringTokenizer(hostName, ".");
				result = tokenizer.nextToken();
				LOGGER.info("Using Hostname:[" + result + "]");
			}
		}

		if (result != null && result.startsWith("-")) {
			LOGGER.warn(String.format(
					"your %s=%s had a \"-\" in front. This is deprecated and should be removed. Autostripping..... ",
					APP_ENVIRONMENT_NAME, result));
			result = result.substring(1, result.length());
		}

		return result;
	}

	private String getForcedName(String name) {
		String forcedEnvName = System.getenv(name);
		if (forcedEnvName == null) {
			// Try system property
			forcedEnvName = System.getProperty(name);
		}
		return forcedEnvName;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	/**
	 * Set the map of hostname regex strings to env ids
	 * 
	 * @param hostNamePatternEnvironmentMap
	 */
	public void setHostNamePatternEnvMap(Map<String, String> hostNamePatternEnvMap) {
		this.hostNamePatternEnvMap = hostNamePatternEnvMap;
		LOGGER.debug("Using hostNamePatternEnvMap:" + hostNamePatternEnvMap);
	}

	public void setEnvironmentVariableMap(Map<String, String> environmentVariableMap) {
		this.environmentVariableMap = environmentVariableMap;
		LOGGER.debug("using environmentVariableMap:" + environmentVariableMap);
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public void setNetService(NetService ns) {
		EnvironmentProperties.NET_SERVICE = ns;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public void setDeveloperHostnamePatternEnvironmentSet(Set<String> developerHostnamePatternEnvironmentSet) {
		this.developerHostnamePatternEnvironmentSet = developerHostnamePatternEnvironmentSet;
	}

}
