package com.naeem.blogs.commons.logging;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class LoggingHelperTest {

	@InjectMocks
	private LoggingHelper logHelper;
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(logHelper);
	}

	
	@Test
	public void getLogger_noParameterIsDefined_returnLogger()
	{
		Logger logger = LoggerFactory.getLogger(LoggingHelper.class);
	
		Assertions.assertThat(logHelper.getLogger()).isEqualTo(logger);
	}
}

