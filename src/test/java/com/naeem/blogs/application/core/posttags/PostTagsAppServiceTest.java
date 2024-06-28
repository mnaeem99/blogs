package com.naeem.blogs.application.core.posttags;

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

import com.naeem.blogs.domain.core.posttags.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.domain.core.posttags.QPostTags;
import com.naeem.blogs.domain.core.posttags.PostTags;
import com.naeem.blogs.domain.core.posttags.PostTagsId;

import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.tags.Tags;
import com.naeem.blogs.domain.core.tags.ITagsRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class PostTagsAppServiceTest {

	@InjectMocks
	@Spy
	protected PostTagsAppService _appService;
	@Mock
	protected IPostTagsRepository _postTagsRepository;
	
    @Mock
	protected IPostsRepository _postsRepository;

    @Mock
	protected ITagsRepository _tagsRepository;

	@Mock
	protected IPostTagsMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    @Mock
    protected PostTagsId postTagsId;
    
    private static final Long ID = 15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findPostTagsById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<PostTags> nullOptional = Optional.ofNullable(null);
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(postTagsId)).isEqualTo(null);
	}
	
	@Test
	public void findPostTagsById_IdIsNotNullAndIdExists_ReturnPostTags() {

		PostTags postTags = mock(PostTags.class);
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTags);
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);
		
	    Assertions.assertThat(_appService.findById(postTagsId)).isEqualTo(_mapper.postTagsToFindPostTagsByIdOutput(postTags));
	}
	
	
	@Test 
    public void createPostTags_PostTagsIsNotNullAndPostTagsDoesNotExist_StorePostTags() { 
 
        PostTags postTagsEntity = mock(PostTags.class); 
    	CreatePostTagsInput postTagsInput = new CreatePostTagsInput();
		
        Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
        postTagsInput.setPostId(15);
		
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
        
		
        Tags tags = mock(Tags.class);
		Optional<Tags> tagsOptional = Optional.of((Tags) tags);
        postTagsInput.setTagId(15);
		
        Mockito.when(_tagsRepository.findById(any(Integer.class))).thenReturn(tagsOptional);
        
		
        Mockito.when(_mapper.createPostTagsInputToPostTags(any(CreatePostTagsInput.class))).thenReturn(postTagsEntity); 
        Mockito.when(_postTagsRepository.save(any(PostTags.class))).thenReturn(postTagsEntity);

	   	Assertions.assertThat(_appService.create(postTagsInput)).isEqualTo(_mapper.postTagsToCreatePostTagsOutput(postTagsEntity));

    } 
    @Test
	public void createPostTags_PostTagsIsNotNullAndPostTagsDoesNotExistAndChildIsNullAndChildIsNotMandatory_StorePostTags() {

		PostTags postTags = mock(PostTags.class);
		CreatePostTagsInput postTagsInput = mock(CreatePostTagsInput.class);
		
		
		Mockito.when(_mapper.createPostTagsInputToPostTags(any(CreatePostTagsInput.class))).thenReturn(postTags);
		Mockito.when(_postTagsRepository.save(any(PostTags.class))).thenReturn(postTags);
	    Assertions.assertThat(_appService.create(postTagsInput)).isEqualTo(_mapper.postTagsToCreatePostTagsOutput(postTags)); 
	}
	
    @Test
	public void updatePostTags_PostTagsIsNotNullAndPostTagsDoesNotExistAndChildIsNullAndChildIsNotMandatory_ReturnUpdatedPostTags() {

		PostTags postTags = mock(PostTags.class);
		UpdatePostTagsInput postTagsInput = mock(UpdatePostTagsInput.class);
		
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTags);
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);
		
		Mockito.when(_mapper.updatePostTagsInputToPostTags(any(UpdatePostTagsInput.class))).thenReturn(postTags);
		Mockito.when(_postTagsRepository.save(any(PostTags.class))).thenReturn(postTags);
		Assertions.assertThat(_appService.update(postTagsId,postTagsInput)).isEqualTo(_mapper.postTagsToUpdatePostTagsOutput(postTags));
	}
	
		
	@Test
	public void updatePostTags_PostTagsIdIsNotNullAndIdExists_ReturnUpdatedPostTags() {

		PostTags postTagsEntity = mock(PostTags.class);
		UpdatePostTagsInput postTags= mock(UpdatePostTagsInput.class);
		
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTagsEntity);
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);
	 		
		Mockito.when(_mapper.updatePostTagsInputToPostTags(any(UpdatePostTagsInput.class))).thenReturn(postTagsEntity);
		Mockito.when(_postTagsRepository.save(any(PostTags.class))).thenReturn(postTagsEntity);
		Assertions.assertThat(_appService.update(postTagsId,postTags)).isEqualTo(_mapper.postTagsToUpdatePostTagsOutput(postTagsEntity));
	}
    
	@Test
	public void deletePostTags_PostTagsIsNotNullAndPostTagsExists_PostTagsRemoved() {

		PostTags postTags = mock(PostTags.class);
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTags);
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);
 		
		_appService.delete(postTagsId); 
		verify(_postTagsRepository).delete(postTags);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<PostTags> list = new ArrayList<>();
		Page<PostTags> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostTagsByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_postTagsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<PostTags> list = new ArrayList<>();
		PostTags postTags = mock(PostTags.class);
		list.add(postTags);
    	Page<PostTags> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostTagsByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.postTagsToFindPostTagsByIdOutput(postTags));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_postTagsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QPostTags postTags = QPostTags.postTagsEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(postTags,map,searchMap)).isEqualTo(builder);
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
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QPostTags postTags = QPostTags.postTagsEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QPostTags.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Posts
	@Test
	public void GetPosts_IfPostTagsIdAndPostsIdIsNotNullAndPostTagsExists_ReturnPosts() {
		PostTags postTags = mock(PostTags.class);
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTags);
		Posts postsEntity = mock(Posts.class);

		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);

		Mockito.when(postTags.getPosts()).thenReturn(postsEntity);
		Assertions.assertThat(_appService.getPosts(postTagsId)).isEqualTo(_mapper.postsToGetPostsOutput(postsEntity, postTags));
	}

	@Test 
	public void GetPosts_IfPostTagsIdAndPostsIdIsNotNullAndPostTagsDoesNotExist_ReturnNull() {
		Optional<PostTags> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPosts(postTagsId)).isEqualTo(null);
	}
   
    //Tags
	@Test
	public void GetTags_IfPostTagsIdAndTagsIdIsNotNullAndPostTagsExists_ReturnTags() {
		PostTags postTags = mock(PostTags.class);
		Optional<PostTags> postTagsOptional = Optional.of((PostTags) postTags);
		Tags tagsEntity = mock(Tags.class);

		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(postTagsOptional);

		Mockito.when(postTags.getTags()).thenReturn(tagsEntity);
		Assertions.assertThat(_appService.getTags(postTagsId)).isEqualTo(_mapper.tagsToGetTagsOutput(tagsEntity, postTags));
	}

	@Test 
	public void GetTags_IfPostTagsIdAndTagsIdIsNotNullAndPostTagsDoesNotExist_ReturnNull() {
		Optional<PostTags> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_postTagsRepository.findById(any(PostTagsId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getTags(postTagsId)).isEqualTo(null);
	}

  
	@Test
	public void ParsePostTagsKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnPostTagsId()
	{
		String keyString= "postId=15,tagId=15";
	
		PostTagsId postTagsId = new PostTagsId();
        postTagsId.setPostId(15);
        postTagsId.setTagId(15);

		Assertions.assertThat(_appService.parsePostTagsKey(keyString)).isEqualToComparingFieldByField(postTagsId);
	}
	
	@Test
	public void ParsePostTagsKey_KeysStringIsEmpty_ReturnNull()
	{
		String keyString= "";
		Assertions.assertThat(_appService.parsePostTagsKey(keyString)).isEqualTo(null);
	}
	
	@Test
	public void ParsePostTagsKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
		String keyString= "postId";

		Assertions.assertThat(_appService.parsePostTagsKey(keyString)).isEqualTo(null);
	}
}
