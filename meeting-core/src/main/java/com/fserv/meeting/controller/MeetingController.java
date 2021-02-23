package com.fserv.meeting.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fserv.meeting.service.TwilioService;
import com.fserv.meeting.vo.RoomDetails;

@RestController
@RequestMapping(value = "/")
public class MeetingController {

	private static Logger LOGGER = Logger.getLogger(MeetingController.class);

	@Autowired
	TwilioService twilioService;

	@RequestMapping(value = "/meetings", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<RoomDetails> createMeeting(@RequestBody Map<String, String> createRoomReqMap) {

		String userId = createRoomReqMap.get("userid");
		String roomName = createRoomReqMap.get("roomName");

		RoomDetails roomDetails = twilioService.createRoom(roomName, userId, true);

		return new ResponseEntity<RoomDetails>(roomDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/meetings/{roomName}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<RoomDetails> getMeetingRoom(@PathVariable("roomName") String roomName,
			@RequestParam(value = "participantName", required = false) String participantName) {
		RoomDetails roomDetails = twilioService.getAndCreateRoomIfNeeded(roomName, participantName, true);
		return new ResponseEntity<RoomDetails>(roomDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/meetings/token", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> getRoomToken() {
		String roomName = "dailyStandUp2";
		String identity = "Raja";
		String token = twilioService.generateRoomAccessToken(roomName, identity);
		Map<String, String> response = new HashMap<String, String>();
		response.put("token", token);
		response.put("identity", identity);
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}
}
