package com.fserv.meeting.properties;

import java.net.InetAddress;

public class NetServiceImpl implements NetService {

	private static final String HOSTNAME_UNKNOW = "localhost";

	@Override
	public String getHostName() {
		String result = HOSTNAME_UNKNOW;
		try {
			result = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
		if (result != null) {
			result = result.toLowerCase();
		}
		return result;
	}

}
