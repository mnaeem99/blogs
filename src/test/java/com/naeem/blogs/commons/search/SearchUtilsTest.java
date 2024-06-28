package com.naeem.blogs.commons.search;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class SearchUtilsTest {

	@InjectMocks
	private SearchUtils searchUtils;

	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(searchUtils);
	}


	@Test
	public void generateSearchCriteriaObject_searchStringIsNotNullAndOperatorIsEquals_returnSearchCriteriaObject()
	{
		String searchString = "permissionId[equals]=1";
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setType(3);
		List<SearchFields> fields = new ArrayList<SearchFields>();

		SearchFields searchField = new SearchFields();

		searchField.setFieldName("permissionId");
		searchField.setOperator("equals");
	
		searchField.setSearchValue("1");
		
		fields.add(searchField);
		searchCriteria.setFields(fields);
		Assertions.assertThat(searchUtils.generateSearchCriteriaObject(searchString)).isEqualToComparingFieldByFieldRecursively(searchCriteria);
	}
	
	@Test
	public void generateSearchCriteriaObject_searchStringIsNotNullAndOperatorIsRange_returnSearchCriteriaObject()
	{
		String searchString = "permissionId[range]=1,10";
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setType(3);
		List<SearchFields> fields = new ArrayList<SearchFields>();

		SearchFields searchField = new SearchFields();

		searchField.setFieldName("permissionId");
		searchField.setOperator("range");
		searchField.setStartingValue("1");
		searchField.setEndingValue("10");
		
		fields.add(searchField);
		searchCriteria.setFields(fields);
		Assertions.assertThat(searchUtils.generateSearchCriteriaObject(searchString)).isEqualToComparingFieldByFieldRecursively(searchCriteria);
	}
	
	@Test
	public void generateSearchCriteriaObject_searchStringIsNull_returnSearchCriteriaObject()
	{
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setType(3);
		List<SearchFields> fields = new ArrayList<SearchFields>();
		searchCriteria.setFields(fields);
		Assertions.assertThat(searchUtils.generateSearchCriteriaObject(null)).isEqualToComparingFieldByFieldRecursively(searchCriteria);
	}

}

