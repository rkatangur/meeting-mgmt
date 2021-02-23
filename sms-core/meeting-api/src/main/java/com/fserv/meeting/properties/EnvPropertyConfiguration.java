package com.fserv.meeting.properties;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ImportResource("classpath*:spring/env-properties.xml")
public class EnvPropertyConfiguration {

	@Bean
	@DependsOn("configuredProperties")
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurerBean(
			@Autowired Properties configuredProperties) {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setProperties(configuredProperties);
		propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertySourcesPlaceholderConfigurer;
	}

}