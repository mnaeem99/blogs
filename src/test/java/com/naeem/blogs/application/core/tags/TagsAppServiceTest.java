package com.naeem.blogs.application.core.tags;

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

import com.naeem.blogs.domain.core.tags.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.domain.core.tags.QTags;
import com.naeem.blogs.domain.core.tags.Tags;

import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TagsAppServiceTest {

	@InjectMocks
	@Spy
	protected TagsAppService _appService;
	@Mock
	protected ITagsRepository _tagsRepository;
	
	@Mock
	protected ITagsMapper _mapper;

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
	public void findTagsById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Tags> nullOptional = Optional.ofNullable(null);
		Mockito.when(_tagsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findTagsById_IdIsNotNullAndIdExists_ReturnTags() {

		Tags tags = mock(Tags.class);
		Optional<Tags> tagsOptional = Optional.of((Tags) tags);
		Mockito.when(_tagsRepository.findById(any(Integer.class))).thenReturn(tagsOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.tagsToFindTagsByIdOutput(tags));
	}
	
	
	@Test 
    public void createTags_TagsIsNotNullAndTagsDoesNotExist_StoreTags() { 
 
        Tags tagsEntity = mock(Tags.class); 
    	CreateTagsInput tagsInput = new CreateTagsInput();
		
        Mockito.when(_mapper.createTagsInputToTags(any(CreateTagsInput.class))).thenReturn(tagsEntity); 
        Mockito.when(_tagsRepository.save(any(Tags.class))).thenReturn(tagsEntity);

	   	Assertions.assertThat(_appService.create(tagsInput)).isEqualTo(_mapper.tagsToCreateTagsOutput(tagsEntity));

    } 
	@Test
	public void updateTags_TagsIdIsNotNullAndIdExists_ReturnUpdatedTags() {

		Tags tagsEntity = mock(Tags.class);
		UpdateTagsInput tags= mock(UpdateTagsInput.class);
		
		Optional<Tags> tagsOptional = Optional.of((Tags) tagsEntity);
		Mockito.when(_tagsRepository.findById(any(Integer.class))).thenReturn(tagsOptional);
	 		
		Mockito.when(_mapper.updateTagsInputToTags(any(UpdateTagsInput.class))).thenReturn(tagsEntity);
		Mockito.when(_tagsRepository.save(any(Tags.class))).thenReturn(tagsEntity);
		Assertions.assertThat(_appService.update(ID,tags)).isEqualTo(_mapper.tagsToUpdateTagsOutput(tagsEntity));
	}
    
	@Test
	public void deleteTags_TagsIsNotNullAndTagsExists_TagsRemoved() {

		Tags tags = mock(Tags.class);
		Optional<Tags> tagsOptional = Optional.of((Tags) tags);
		Mockito.when(_tagsRepository.findById(any(Integer.class))).thenReturn(tagsOptional);
 		
		_appService.delete(ID); 
		verify(_tagsRepository).delete(tags);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Tags> list = new ArrayList<>();
		Page<Tags> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindTagsByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_tagsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Tags> list = new ArrayList<>();
		Tags tags = mock(Tags.class);
		list.add(tags);
    	Page<Tags> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindTagsByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.tagsToFindTagsByIdOutput(tags));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_tagsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QTags tags = QTags.tagsEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("name",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(tags.name.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(tags,map,searchMap)).isEqualTo(builder);
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
        list.add("name");
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QTags tags = QTags.tagsEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setFieldName("name");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(tags.name.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QTags.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}

	@Test
	public void ParsepostTagsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("tagId", keyString);
		Assertions.assertThat(_appService.parsePostTagsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
