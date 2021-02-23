package com.fserv.meeting.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.Resource;

public class EnvironmentPropertiesFactoryBean extends PropertiesFactoryBean {

	private static final Logger LOGGER = Logger.getLogger(EnvironmentPropertiesFactoryBean.class);

	private EnvironmentProperties environmentProperties;

	public void setLocationPaths(String locations) {
		setLocationPathsInternal(locations, false);
	}

	public void setLocationPathsSystem(String locations) {
		setLocationPathsInternal(locations, true);
	}

	private void setLocationPathsInternal(String locations, boolean setSystem) {
		LOGGER.info("setting location paths:" + locations);
		String[] paths = locations.split(",");
		List<Resource> resources = new ArrayList<Resource>();
		Resource overrideResource = environmentProperties.getOverrideResourceLocation();
		if (overrideResource != null) {
			resources.add(overrideResource);
		} else {
			if (paths != null) {
				for (String path : paths) {
					Resource hostlessLocation = environmentProperties.getHostlessResourceLocation(path.trim());
					Resource location = environmentProperties.getResourceLocation(path.trim(), setSystem);
					if (hostlessLocation != null) {
						if (!hostlessLocation.equals(location)) {
							resources.add(hostlessLocation);
						}
					}
					resources.add(location);
				}
			}
		}

		if (!setSystem) {
			super.setLocations(resources.toArray(new Resource[0]));
		}
	}

	public void setEnvironmentProperties(EnvironmentProperties environmentProperties) {
		this.environmentProperties = environmentProperties;
	}

}
