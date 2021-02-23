package com.fserv.sms.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fserv.sms.service.SmsService;
import com.fserv.sms.vo.MessageDetails;

@RestController
@RequestMapping(value = "/sms")
public class SmsController {

	private static Logger LOGGER = Logger.getLogger(SmsController.class);

	@Autowired
	SmsService smsService;

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<MessageDetails> sendMessage(@RequestBody Map<String, String> sendMessageReqMap) {
		
		LOGGER.info("SMS API send message");
		
		String fromNumber = sendMessageReqMap.get("fromNumber");
		String toNumber = sendMessageReqMap.get("toNumber");
		String content = sendMessageReqMap.get("content");

		MessageDetails result = smsService.sendMessage(fromNumber, toNumber, content);

		return new ResponseEntity<MessageDetails>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/track", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<MessageDetails> trackMessageStatus(@RequestBody Map<String, String> sendMessageReqMap) {
		
		LOGGER.info("SMS API track message");
		
		String fromNumber = sendMessageReqMap.get("fromNumber");
		String toNumber = sendMessageReqMap.get("toNumber");
		String content = sendMessageReqMap.get("content");

		MessageDetails result = smsService.trackMessageStatus(fromNumber, toNumber, content);

		return new ResponseEntity<MessageDetails>(result, HttpStatus.OK);
	}
}
