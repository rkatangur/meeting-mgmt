package com.fserv.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class } )
////@EnableAutoConfiguration
public class TwillioApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwillioApplication.class, args);
//		createRoom();
	}

//	private static String twilio_account_sid = "AC1099d28da50686495d55cc04b7a7db57";
//	private static String twilio_account_authtoken = "2e05d60fb814ae9a9e82729d8158a211";
//
//	public static void createRoom() {
//		Twilio.init(twilio_account_sid, twilio_account_authtoken);
//		Room room  = Room.fetcher("TestUniqueRoom").fetch();
//		System.out.println(JsonUtils.writeValueAsString(room));
//
////		Room newRoom = Room.creator().setUniqueName("TestUniqueRoom").create();
////		System.out.println(newRoom.getAccountSid());
////		System.out.println(JsonUtils.writeValueAsString(newRoom));
//	}

}
