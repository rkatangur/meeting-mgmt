package com.fserv.meeting.exception;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import com.fserv.common.web.exception.RestExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends RestExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(SQLException.class)
	public String handleSQLException(HttpServletRequest request, Exception ex) {
		logger.info("SQLException Occured:: URL=" + request.getRequestURL());
		return "database_error";
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "IOException occured")
	@ExceptionHandler(IOException.class)
	public void handleIOException(HttpServletRequest request, Exception ex) {
		logger.error("IOException handler executed", ex);
		// returning 404 error code
	}

//	@ResponseStatus(value = HttpStatus.OK, reason = "Provider application not available")
	@ExceptionHandler(RestClientException.class)
	public ModelAndView handleRestClientException(HttpServletRequest request, Exception ex) {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("exception", ex);
		modelAndView.addObject("url", request.getRequestURL());
		modelAndView.addObject("message", "ephase provider application is not available");

		modelAndView.setViewName("error");
		logger.error("Provider application not available", ex);
		return modelAndView;
	}
}