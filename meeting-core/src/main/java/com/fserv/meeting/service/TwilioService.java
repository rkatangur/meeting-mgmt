package com.fserv.meeting.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fserv.meeting.vo.RoomDetails;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;
import com.twilio.rest.video.v1.Room;

@Service
public class TwilioService {

	private static Logger LOGGER = LoggerFactory.getLogger(TwilioService.class);

	@Value("${twilio.account.sid}")
	private String twilioAccountSid;

	@Value("${twilio.account.authtoken}")
	private String twilioAccountAuthtoken;

	@Value("${twilio.account.apikeysid}")
	private String twilioAccountApikeysid;

	@Value("${twilio.account.apikeysecret}")
	private String twilioAccountApikeysecret;

	@PostConstruct
	public void init() {
		LOGGER.info("Twilio init using twilioAccountSid " + twilioAccountSid + ", twilioAccountAuthtoken "
				+ twilioAccountAuthtoken);
		Twilio.init(twilioAccountSid, twilioAccountAuthtoken);
		Twilio.getRestClient();
	}

	public RoomDetails createRoom(String roomName, String userId, boolean generateAccessToken) {
		Room room = Room.creator().setUniqueName(roomName).create();
		RoomDetails roomDetails = buildRoomDetails(room);
		if (generateAccessToken) {
			roomDetails.setToken(generateRoomAccessToken(roomName, userId));
			roomDetails.setIdentity(userId);
		}
		return roomDetails;
	}

	public RoomDetails buildRoomDetails(Room room) {
		RoomDetails roomDetails = new RoomDetails();
		roomDetails.setRoomName(room.getUniqueName());
		roomDetails.setRoomId(room.getSid());
		roomDetails.setRoomStatus(room.getStatus());
		roomDetails.setRoomType(room.getType());
		roomDetails.setDuration(room.getDuration());

		if (room.getEndTime() != null)
			roomDetails.setEndTime(room.getEndTime().getMillis());

		if (room.getDateCreated() != null)
			roomDetails.setDateCreated(room.getDateCreated().getMillis());

		if (room.getDateUpdated() != null)
			roomDetails.setDateUpdated(room.getDateUpdated().getMillis());

		roomDetails.setUrl(room.getUrl().toString());
		return roomDetails;
	}

	public RoomDetails getAndCreateRoomIfNeeded(String roomName, String userId, boolean generateAccessToken) {
		Room room = null;
		try {
			room = fetchRoom(roomName);
			RoomDetails roomDetails = buildRoomDetails(room);
			if (generateAccessToken) {
				roomDetails.setToken(generateRoomAccessToken(roomName, userId));
				roomDetails.setIdentity(userId);
			}
			return roomDetails;
		} catch (ApiException ape) {
			LOGGER.info("Exception on fetching room " + roomName + ", errorMsg " + ape.getMessage());
			return createRoom(roomName, userId, generateAccessToken);
		}
	}

	private Room fetchRoom(String roomName) {
		return Room.fetcher(roomName).fetch();
	}

	public String generateRoomAccessToken(String roomName, String userId) {
		final VideoGrant grant = new VideoGrant();
		if (!StringUtils.isBlank(roomName))
			grant.setRoom(roomName);

		// Create an Access Token
//		final AccessToken token = new AccessToken.Builder(twilioAccountSid, twilioAccountApikeysid, twilioAccountApikeysecret).
		final AccessToken.Builder tokenBuilder = new AccessToken.Builder(twilioAccountSid, twilioAccountApikeysid,
				twilioAccountApikeysecret);

		if (!StringUtils.isBlank(userId))
			tokenBuilder.identity(userId); // Set the Identity of this token

		AccessToken token = tokenBuilder.grant(grant) // Grant access to Video
				.build();
		// Serialize the token as a JWT
		return token.toJwt();
	}

	public String generateRoomAccessToken() {
		return generateRoomAccessToken(null, null);
	}

	public String getTwilioAccountSid() {
		return twilioAccountSid;
	}

	public void setTwilioAccountSid(String twilioAccountSid) {
		this.twilioAccountSid = twilioAccountSid;
	}

	public String getTwilioAccountAuthtoken() {
		return twilioAccountAuthtoken;
	}

	public void setTwilioAccountAuthtoken(String twilioAccountAuthtoken) {
		this.twilioAccountAuthtoken = twilioAccountAuthtoken;
	}

	public String getTwilioAccountApikeysid() {
		return twilioAccountApikeysid;
	}

	public void setTwilioAccountApikeysid(String twilioAccountApikeysid) {
		this.twilioAccountApikeysid = twilioAccountApikeysid;
	}

	public String getTwilioAccountApikeysecret() {
		return twilioAccountApikeysecret;
	}

	public void setTwilioAccountApikeysecret(String twilioAccountApikeysecret) {
		this.twilioAccountApikeysecret = twilioAccountApikeysecret;
	}
}
