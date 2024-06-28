package com.naeem.blogs.application.core.categories;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naeem.blogs.domain.core.categories.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.categories.dto.*;
import com.naeem.blogs.domain.core.categories.QCategories;
import com.naeem.blogs.domain.core.categories.Categories;

import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CategoriesAppServiceTest {

	@InjectMocks
	@Spy
	protected CategoriesAppService _appService;
	@Mock
	protected ICategoriesRepository _categoriesRepository;
	
	@Mock
	protected ICategoriesMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    protected static Integer ID=15;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findCategoriesById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Categories> nullOptional = Optional.ofNullable(null);
		Mockito.when(_categoriesRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findCategoriesById_IdIsNotNullAndIdExists_ReturnCategories() {

		Categories categories = mock(Categories.class);
		Optional<Categories> categoriesOptional = Optional.of((Categories) categories);
		Mockito.when(_categoriesRepository.findById(any(Integer.class))).thenReturn(categoriesOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.categoriesToFindCategoriesByIdOutput(categories));
	}
	
	
	@Test 
    public void createCategories_CategoriesIsNotNullAndCategoriesDoesNotExist_StoreCategories() { 
 
        Categories categoriesEntity = mock(Categories.class); 
    	CreateCategoriesInput categoriesInput = new CreateCategoriesInput();
		
        Mockito.when(_mapper.createCategoriesInputToCategories(any(CreateCategoriesInput.class))).thenReturn(categoriesEntity); 
        Mockito.when(_categoriesRepository.save(any(Categories.class))).thenReturn(categoriesEntity);

	   	Assertions.assertThat(_appService.create(categoriesInput)).isEqualTo(_mapper.categoriesToCreateCategoriesOutput(categoriesEntity));

    } 
	@Test
	public void updateCategories_CategoriesIdIsNotNullAndIdExists_ReturnUpdatedCategories() {

		Categories categoriesEntity = mock(Categories.class);
		UpdateCategoriesInput categories= mock(UpdateCategoriesInput.class);
		
		Optional<Categories> categoriesOptional = Optional.of((Categories) categoriesEntity);
		Mockito.when(_categoriesRepository.findById(any(Integer.class))).thenReturn(categoriesOptional);
	 		
		Mockito.when(_mapper.updateCategoriesInputToCategories(any(UpdateCategoriesInput.class))).thenReturn(categoriesEntity);
		Mockito.when(_categoriesRepository.save(any(Categories.class))).thenReturn(categoriesEntity);
		Assertions.assertThat(_appService.update(ID,categories)).isEqualTo(_mapper.categoriesToUpdateCategoriesOutput(categoriesEntity));
	}
    
	@Test
	public void deleteCategories_CategoriesIsNotNullAndCategoriesExists_CategoriesRemoved() {

		Categories categories = mock(Categories.class);
		Optional<Categories> categoriesOptional = Optional.of((Categories) categories);
		Mockito.when(_categoriesRepository.findById(any(Integer.class))).thenReturn(categoriesOptional);
 		
		_appService.delete(ID); 
		verify(_categoriesRepository).delete(categories);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Categories> list = new ArrayList<>();
		Page<Categories> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindCategoriesByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_categoriesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Categories> list = new ArrayList<>();
		Categories categories = mock(Categories.class);
		list.add(categories);
    	Page<Categories> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindCategoriesByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.categoriesToFindCategoriesByIdOutput(categories));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_categoriesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QCategories categories = QCategories.categoriesEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("description",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(categories.description.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(categories,map,searchMap)).isEqualTo(builder);
	}
	
	@Test (expected = Exception.class)
	public void checkProperties_PropertyDoesNotExist_ThrowException() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("xyz");
		_appService.checkProperties(list);
	}
	
	@Test
	public void checkProperties_PropertyExists_ReturnNothing() throws Exception {
		List<String> list = new ArrayList<>();
        list.add("description");
        list.add("name");
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QCategories categories = QCategories.categoriesEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setFieldName("description");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(categories.description.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QCategories.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}

	@Test
	public void ParsepostCategoriesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("categoryId", keyString);
		Assertions.assertThat(_appService.parsePostCategoriesJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
