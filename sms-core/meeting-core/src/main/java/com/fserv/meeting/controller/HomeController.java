package com.fserv.meeting.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fserv.meeting.exception.APIException;

@Controller
@RequestMapping(value = "/")
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(method = { RequestMethod.GET }, value = "/health")
	public ResponseEntity<String> health() {
		String msg = "Meeting management API service health.";
		logger.info(msg);
		return new ResponseEntity<String>(msg, HttpStatus.OK);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "homeredirect")
	public String testRedirectView() {
		logger.info("home Controller test redirect view");
		return "redirect:login";
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,
			RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> doGetAjax() {
		return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
	}

	// Testing exception handling codes in the web application.
	@RequestMapping(method = { RequestMethod.GET }, value = "testapiexception")
	public String testApiExceptionHandling() {
		logger.info("home Controller throwing an ApiException.");
		throw new APIException("home Controller throwing ApiException.");
	}

	/**
	 * Uses the global RestExceptionHandler of type ResponseEntityExceptionHandler
	 * to handle NullPointerException and transforms that to JSON.
	 * 
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = "testruntimeexception")
	public String testRuntimeExceptionHandling() {
		logger.info("home Controller throwing an NullPointerException.");
		throw new NullPointerException("home Controller throwing NullPointerException.");
	}

	/**
	 * Using error.jsp to render the Exeception stacktrace.
	 * 
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = "testillegalArgexception")
	public String testIllegalArgumentExceptionHandling() {
		logger.info("home Controller throwing an NullPointerException.");
		throw new IllegalArgumentException("home Controller throwing IllegalArgumentException.");
	}

	/**
	 * uses 404.jsp in GlobalExceptionHandler as it throws an IOException.
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = "testioexception")
	public String testIOExceptionHandling() throws Exception {
		logger.info("home Controller throwing an IOException.");
		throw new IOException("home Controller throwing IOException.");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView handleIllegalArgumentException(HttpServletRequest request, Exception ex) {
		logger.error("Requested URL=" + request.getRequestURL());
		logger.error("Exception Raised=" + ex);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("exception", ex);
		modelAndView.addObject("url", request.getRequestURL());

		modelAndView.setViewName("error");
		return modelAndView;
	}
}
