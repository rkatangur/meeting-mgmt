package com.fserv.meeting.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.twilio.rest.video.v1.Room.RoomStatus;
import com.twilio.rest.video.v1.Room.RoomType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDetails implements ResourceVo {
	private String roomName;
	private String roomId;
	private String token;
	private String identity;
	private RoomStatus roomStatus;
	private RoomType roomType;
	private Long dateCreated;
	private Long dateUpdated;
	private String url;
	private Map<String, String> links;
	private Integer duration;
	private Long endTime;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public RoomStatus getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(RoomStatus roomStatus) {
		this.roomStatus = roomStatus;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	public Long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Long dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public Long getEndTime() {
		return endTime;
	}
}
