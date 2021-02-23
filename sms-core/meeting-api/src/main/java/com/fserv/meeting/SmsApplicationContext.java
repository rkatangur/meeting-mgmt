package com.fserv.meeting;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fserv.meeting.properties.EnvPropertyConfiguration;

@Configuration
@Import({ EnvPropertyConfiguration.class })
@ComponentScan(basePackages = { "com.fserv.sms.**" })
public class SmsApplicationContext {

}
