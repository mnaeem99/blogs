package com.naeem.blogs.application.core.posts;

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

import com.naeem.blogs.domain.core.posts.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.domain.core.posts.QPosts;
import com.naeem.blogs.domain.core.posts.Posts;

import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class PostsAppServiceTest {

	@InjectMocks
	@Spy
	protected PostsAppService _appService;
	@Mock
	protected IPostsRepository _postsRepository;
	
    @Mock
	protected IUsersRepository _usersRepository;

	@Mock
	protected IPostsMapper _mapper;

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
	public void findPostsById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Posts> nullOptional = Optional.ofNullable(null);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findPostsById_IdIsNotNullAndIdExists_ReturnPosts() {

		Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.postsToFindPostsByIdOutput(posts));
	}
	
	
	@Test 
    public void createPosts_PostsIsNotNullAndPostsDoesNotExist_StorePosts() { 
 
        Posts postsEntity = mock(Posts.class); 
    	CreatePostsInput postsInput = new CreatePostsInput();
		
        Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
        postsInput.setAuthorId(15);
		
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);
        
		
        Mockito.when(_mapper.createPostsInputToPosts(any(CreatePostsInput.class))).thenReturn(postsEntity); 
        Mockito.when(_postsRepository.save(any(Posts.class))).thenReturn(postsEntity);

	   	Assertions.assertThat(_appService.create(postsInput)).isEqualTo(_mapper.postsToCreatePostsOutput(postsEntity));

    } 
	@Test
	public void createPosts_PostsIsNotNullAndPostsDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreatePostsInput posts = mock(CreatePostsInput.class);
		
		Mockito.when(_mapper.createPostsInputToPosts(any(CreatePostsInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(posts)).isEqualTo(null);
	}
	
	@Test
	public void createPosts_PostsIsNotNullAndPostsDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreatePostsInput posts = new CreatePostsInput();
	    
        posts.setAuthorId(15);
     
     	Optional<Users> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
//		Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(posts)).isEqualTo(null);
    }
    
    @Test
	public void updatePosts_PostsIsNotNullAndPostsDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdatePostsInput postsInput = mock(UpdatePostsInput.class);
		Posts posts = mock(Posts.class); 
		
     	Optional<Posts> postsOptional = Optional.of((Posts) posts);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
		
		Mockito.when(_mapper.updatePostsInputToPosts(any(UpdatePostsInput.class))).thenReturn(posts); 
		Assertions.assertThat(_appService.update(ID,postsInput)).isEqualTo(null);
	}
	
	@Test
	public void updatePosts_PostsIsNotNullAndPostsDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdatePostsInput postsInput = new UpdatePostsInput();
        postsInput.setAuthorId(15);
     
    	Posts posts = mock(Posts.class);
		
     	Optional<Posts> postsOptional = Optional.of((Posts) posts);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
		
		Mockito.when(_mapper.updatePostsInputToPosts(any(UpdatePostsInput.class))).thenReturn(posts);
     	Optional<Users> nullOptional = Optional.ofNullable(null);
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(ID,postsInput)).isEqualTo(null);
	}

		
	@Test
	public void updatePosts_PostsIdIsNotNullAndIdExists_ReturnUpdatedPosts() {

		Posts postsEntity = mock(Posts.class);
		UpdatePostsInput posts= mock(UpdatePostsInput.class);
		
		Optional<Posts> postsOptional = Optional.of((Posts) postsEntity);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
	 		
		Mockito.when(_mapper.updatePostsInputToPosts(any(UpdatePostsInput.class))).thenReturn(postsEntity);
		Mockito.when(_postsRepository.save(any(Posts.class))).thenReturn(postsEntity);
		Assertions.assertThat(_appService.update(ID,posts)).isEqualTo(_mapper.postsToUpdatePostsOutput(postsEntity));
	}
    
	@Test
	public void deletePosts_PostsIsNotNullAndPostsExists_PostsRemoved() {

		Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
 		
		_appService.delete(ID); 
		verify(_postsRepository).delete(posts);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Posts> list = new ArrayList<>();
		Page<Posts> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostsByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_postsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Posts> list = new ArrayList<>();
		Posts posts = mock(Posts.class);
		list.add(posts);
    	Page<Posts> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostsByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.postsToFindPostsByIdOutput(posts));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_postsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QPosts posts = QPosts.postsEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("content",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(posts.content.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(posts,map,searchMap)).isEqualTo(builder);
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
        list.add("content");
        list.add("title");
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QPosts posts = QPosts.postsEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setFieldName("content");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(posts.content.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QPosts.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Users
	@Test
	public void GetUsers_IfPostsIdAndUsersIdIsNotNullAndPostsExists_ReturnUsers() {
		Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
		Users usersEntity = mock(Users.class);

		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);

		Mockito.when(posts.getUsers()).thenReturn(usersEntity);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(_mapper.usersToGetUsersOutput(usersEntity, posts));
	}

	@Test 
	public void GetUsers_IfPostsIdAndUsersIdIsNotNullAndPostsDoesNotExist_ReturnNull() {
		Optional<Posts> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(null);
	}

	@Test
	public void ParsecommentsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("postId", keyString);
		Assertions.assertThat(_appService.parseCommentsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParselikesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("postId", keyString);
		Assertions.assertThat(_appService.parseLikesJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParsepostCategoriesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("postId", keyString);
		Assertions.assertThat(_appService.parsePostCategoriesJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParsepostTagsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("postId", keyString);
		Assertions.assertThat(_appService.parsePostTagsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
