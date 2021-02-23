package com.fserv.meeting.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

//    @JsonSerialize(using = CustomDateSerializer.class)

public class CustomDateSerializer extends JsonSerializer<Date> {
	private SimpleDateFormat formatter;

	public CustomDateSerializer() {
		super();
		formatter = new SimpleDateFormat("dd-MM-yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		gen.writeString(formatter.format(value));
	}
}