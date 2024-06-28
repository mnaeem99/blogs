package com.naeem.blogs.commons.search;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class OffsetBasedPageRequestTest {

	@Before 
	public void setUp() {

	}

	
	@Test
	public void offestBasedPageRequest_offestIsLessThanZero_throwException()
	{
		
		try {
			new OffsetBasedPageRequest(-1, 1, Sort.by(Sort.Direction.ASC, "id"));
        } catch (IllegalArgumentException ex) {
            Assertions.assertThat(ex.getMessage()).isEqualTo("Offset index must not be less than zero!");
        }
		
	}
	
	@Test
	public void offestBasedPageRequest_limitIsLessThanZero_throwException()
	{
		
		try {
			new OffsetBasedPageRequest(1, -1, Sort.by(Sort.Direction.ASC, "id"));
        } catch (IllegalArgumentException ex) {
            Assertions.assertThat(ex.getMessage()).isEqualTo("Limit must not be less than one!");
        }
		
	}

}

