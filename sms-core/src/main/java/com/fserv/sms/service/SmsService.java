package com.fserv.sms.service;

import org.springframework.stereotype.Service;

import com.fserv.sms.vo.MessageDetails;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;

@Service
public class SmsService {
	// Find your Account Sid and Auth Token at twilio.com/console
	public static final String ACCOUNT_SID = "ACdf645a6a396aedd7b48769ac43e27de6";
	public static final String AUTH_TOKEN = "cb975b6122475e1ecfc0cb6172e19d89";

	public MessageDetails sendMessage(String fromNumber, String toNumber, String content) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		MessageDetails returnMessage = new MessageDetails();
		returnMessage.setFromNumber(fromNumber);
		returnMessage.setToNumber(toNumber);
		returnMessage.setContent(content);

		Message message = Message.creator(
				new PhoneNumber(toNumber), // to
				new PhoneNumber(fromNumber), // from
				content).create();

		System.out.println(message.getSid());

		return returnMessage;
	}
	
	// toNumber: your mobile number
	// fromNumber: Twilio number
	public MessageDetails trackMessageStatus(String fromNumber, String toNumber, String content) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		MessageDetails returnMessage = new MessageDetails();
		returnMessage.setFromNumber(fromNumber);
		returnMessage.setToNumber(toNumber);
		returnMessage.setContent(content);
		
		Message message = Message.creator(
                new PhoneNumber(toNumber),
                new PhoneNumber(fromNumber),
                content)
            .setStatusCallback(URI.create("https://postb.in/b/1599691326585-0380046991631"))
            .create();

		System.out.println(message.getSid());

		return returnMessage;
	}
}
