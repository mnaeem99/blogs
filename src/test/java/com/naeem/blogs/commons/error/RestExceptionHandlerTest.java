package com.naeem.blogs.commons.error;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.naeem.blogs.commons.logging.LoggingHelper;

@RunWith(SpringJUnit4ClassRunner.class)
public class RestExceptionHandlerTest {

	@InjectMocks
	@Spy
	private RestExceptionHandler restExceptionHandler;

	@Mock
	private ApiError mockedApi;

	@Mock
	private RestExceptionHandler mockRestExceptionHandler;
	@Mock
	private Logger loggerMock;

	@Mock
	private LoggingHelper logHelper;

	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(restExceptionHandler);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

	}


	@Test
	public void shouldBeAbleToHandleMissingServletRequestParameter()
	{
		MissingServletRequestParameterException ex = new MissingServletRequestParameterException("userId", "Long");
		HttpHeaders headers = new HttpHeaders();

		ApiError apiError = new ApiError(BAD_REQUEST, ex.getParameterName() + " parameter is missing", ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleMissingServletRequestParameter(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHttpMediaTypeNotSupportedException()
	{
		HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Invalid");
		HttpHeaders headers = new HttpHeaders();

		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

		ApiError apiError=new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleHttpMediaTypeNotSupported(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleMethodArgumentNotValidException()
	{

		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(new FieldError("station", "name", "should not be empty")));
		when(ex.getBindingResult()).thenReturn(bindingResult);

		HttpHeaders headers = new HttpHeaders();

		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(Arrays.asList(new FieldError("station", "name", "should not be empty")));
		apiError.addValidationError(bindingResult.getGlobalErrors());
		ResponseEntity<Object> expected = restExceptionHandler.handleMethodArgumentNotValid(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void shouldBeAbleToHandleHttpMessageNotReadable()
	{

		HttpMessageNotReadableException ex =new HttpMessageNotReadableException("message");
		HttpHeaders headers = new HttpHeaders();
		String error = "Malformed JSON request";
		ApiError apiError=new ApiError(BAD_REQUEST, error, ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleHttpMessageNotReadable(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleHttpMessageNotWritableException()
	{
		HttpMessageNotWritableException ex =new HttpMessageNotWritableException("message");
		HttpHeaders headers = new HttpHeaders();
		String error = "Error writing JSON output";
		ApiError apiError=new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleHttpMessageNotWritable(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleNoHandlerFoundException()
	{
		HttpHeaders headers = new HttpHeaders();
		NoHandlerFoundException ex =new NoHandlerFoundException("method", "url", headers);
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
		apiError.setDebugMessage(ex.getMessage());
		ResponseEntity<Object> expected = restExceptionHandler.handleNoHandlerFoundException(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleHttpRequestMethodNotSupportedException()
	{
		HttpHeaders headers = new HttpHeaders();
		HttpRequestMethodNotSupportedException ex =new HttpRequestMethodNotSupportedException("method");
		ApiError apiError = new ApiError(METHOD_NOT_ALLOWED);
		apiError.setMessage("Specified HTTP Method Is Not Allowed");
		apiError.setDebugMessage(ex.getMessage());
		ResponseEntity<Object> expected = restExceptionHandler.handleHttpRequestMethodNotSupported(ex, headers,null, null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleConstraintViolationException()
	{

		Set<javax.validation.ConstraintViolation<?>> violations = new HashSet<javax.validation.ConstraintViolation<?>>();
		javax.validation.ConstraintViolationException ex =new javax.validation.ConstraintViolationException("message", violations);
		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage("Validation error");
		apiError.addValidationErrors(ex.getConstraintViolations());

		ResponseEntity<Object> expected = restExceptionHandler.handleConstraintViolation(ex);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleEntityNotFound()
	{
		EntityNotFoundException ex =new EntityNotFoundException();
		ApiError apiError = new ApiError(NOT_FOUND);
		apiError.setMessage(ex.getMessage());

		ResponseEntity<Object> expected = restExceptionHandler.handleEntityNotFound(ex);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleEntityExists()
	{
		EntityExistsException ex =new EntityExistsException();
		ApiError apiError = new ApiError(CONFLICT);
		apiError.setMessage(ex.getMessage());
		ResponseEntity<Object> expected = restExceptionHandler.handleEntityExists(ex);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleDataIntegrityViolationWithInternalServerError()
	{
		DataIntegrityViolationException ex =new DataIntegrityViolationException("message");
		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleDataIntegrityViolation(ex,null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleDataIntegrityViolationWithConstraintVoilation()
	{
		DataIntegrityViolationException ex =new DataIntegrityViolationException("message", new ConstraintViolationException("message", new SQLException(), "Not null"));

		ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), ex.getCause());
		ResponseEntity<Object> expected = restExceptionHandler.handleDataIntegrityViolation(ex,null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleMethodArgumentTypeMismatch() throws ClassNotFoundException
	{
		MethodParameter methodParam = mock(MethodParameter.class);
		MethodArgumentTypeMismatchException ex =new MethodArgumentTypeMismatchException("aa", this.getClass(), "name", methodParam, null);

		ApiError apiError = new ApiError(BAD_REQUEST);
		apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
		apiError.setDebugMessage(ex.getMessage());

		ResponseEntity<Object> expected = restExceptionHandler.handleMethodArgumentTypeMismatch(ex,null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	@Test
	public void shouldBeAbleToHandleAnyException()
	{
		MethodArgumentTypeMismatchException ex =mock (MethodArgumentTypeMismatchException.class);

		String error = "Internal error occured" ;
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, error, ex);
		ResponseEntity<Object> expected = restExceptionHandler.handleAnyException(ex,null);

		setTimestampValueAndverifyResponseObject(apiError, expected);
	}

	private void setTimestampValueAndverifyResponseObject(ApiError actual, ResponseEntity<Object> expected)
	{
		ApiError apiError= (ApiError) expected.getBody();
		actual.setTimestamp(apiError.getTimestamp());
		ResponseEntity<Object> response = new ResponseEntity<>(actual, actual.getStatus());
		Assertions.assertThat(expected).isEqualToComparingFieldByFieldRecursively(response);
	}

}

