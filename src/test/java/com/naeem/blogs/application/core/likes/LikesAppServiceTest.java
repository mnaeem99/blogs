package com.naeem.blogs.application.core.likes;

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

import com.naeem.blogs.domain.core.likes.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.domain.core.likes.QLikes;
import com.naeem.blogs.domain.core.likes.Likes;

import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class LikesAppServiceTest {

	@InjectMocks
	@Spy
	protected LikesAppService _appService;
	@Mock
	protected ILikesRepository _likesRepository;
	
    @Mock
	protected IPostsRepository _postsRepository;

    @Mock
	protected IUsersRepository _usersRepository;

	@Mock
	protected ILikesMapper _mapper;

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
	public void findLikesById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Likes> nullOptional = Optional.ofNullable(null);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findLikesById_IdIsNotNullAndIdExists_ReturnLikes() {

		Likes likes = mock(Likes.class);
		Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.likesToFindLikesByIdOutput(likes));
	}
	
	
	@Test 
    public void createLikes_LikesIsNotNullAndLikesDoesNotExist_StoreLikes() { 
 
        Likes likesEntity = mock(Likes.class); 
    	CreateLikesInput likesInput = new CreateLikesInput();
		
        Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
        likesInput.setPostId(15);
		
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
        
		
        Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
        likesInput.setUserId(15);
		
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);
        
		
        Mockito.when(_mapper.createLikesInputToLikes(any(CreateLikesInput.class))).thenReturn(likesEntity); 
        Mockito.when(_likesRepository.save(any(Likes.class))).thenReturn(likesEntity);

	   	Assertions.assertThat(_appService.create(likesInput)).isEqualTo(_mapper.likesToCreateLikesOutput(likesEntity));

    } 
	@Test
	public void createLikes_LikesIsNotNullAndLikesDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreateLikesInput likes = mock(CreateLikesInput.class);
		
		Mockito.when(_mapper.createLikesInputToLikes(any(CreateLikesInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(likes)).isEqualTo(null);
	}
	
	@Test
	public void createLikes_LikesIsNotNullAndLikesDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreateLikesInput likes = new CreateLikesInput();
	    
        likes.setPostId(15);
     
     	Optional<Posts> nullOptional = Optional.ofNullable(null);
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
        
//		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(likes)).isEqualTo(null);
    }
    
    @Test
	public void updateLikes_LikesIsNotNullAndLikesDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdateLikesInput likesInput = mock(UpdateLikesInput.class);
		Likes likes = mock(Likes.class); 
		
     	Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);
		
		Mockito.when(_mapper.updateLikesInputToLikes(any(UpdateLikesInput.class))).thenReturn(likes); 
		Assertions.assertThat(_appService.update(ID,likesInput)).isEqualTo(null);
	}
	
	@Test
	public void updateLikes_LikesIsNotNullAndLikesDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdateLikesInput likesInput = new UpdateLikesInput();
        likesInput.setPostId(15);
     
    	Likes likes = mock(Likes.class);
		
     	Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);
		
		Mockito.when(_mapper.updateLikesInputToLikes(any(UpdateLikesInput.class))).thenReturn(likes);
     	Optional<Posts> nullOptional = Optional.ofNullable(null);
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(ID,likesInput)).isEqualTo(null);
	}

		
	@Test
	public void updateLikes_LikesIdIsNotNullAndIdExists_ReturnUpdatedLikes() {

		Likes likesEntity = mock(Likes.class);
		UpdateLikesInput likes= mock(UpdateLikesInput.class);
		
		Optional<Likes> likesOptional = Optional.of((Likes) likesEntity);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);
	 		
		Mockito.when(_mapper.updateLikesInputToLikes(any(UpdateLikesInput.class))).thenReturn(likesEntity);
		Mockito.when(_likesRepository.save(any(Likes.class))).thenReturn(likesEntity);
		Assertions.assertThat(_appService.update(ID,likes)).isEqualTo(_mapper.likesToUpdateLikesOutput(likesEntity));
	}
    
	@Test
	public void deleteLikes_LikesIsNotNullAndLikesExists_LikesRemoved() {

		Likes likes = mock(Likes.class);
		Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);
 		
		_appService.delete(ID); 
		verify(_likesRepository).delete(likes);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Likes> list = new ArrayList<>();
		Page<Likes> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindLikesByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_likesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Likes> list = new ArrayList<>();
		Likes likes = mock(Likes.class);
		list.add(likes);
    	Page<Likes> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindLikesByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.likesToFindLikesByIdOutput(likes));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_likesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QLikes likes = QLikes.likesEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(likes,map,searchMap)).isEqualTo(builder);
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
		QLikes likes = QLikes.likesEntity;
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
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QLikes.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Posts
	@Test
	public void GetPosts_IfLikesIdAndPostsIdIsNotNullAndLikesExists_ReturnPosts() {
		Likes likes = mock(Likes.class);
		Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Posts postsEntity = mock(Posts.class);

		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);

		Mockito.when(likes.getPosts()).thenReturn(postsEntity);
		Assertions.assertThat(_appService.getPosts(ID)).isEqualTo(_mapper.postsToGetPostsOutput(postsEntity, likes));
	}

	@Test 
	public void GetPosts_IfLikesIdAndPostsIdIsNotNullAndLikesDoesNotExist_ReturnNull() {
		Optional<Likes> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPosts(ID)).isEqualTo(null);
	}
   
    //Users
	@Test
	public void GetUsers_IfLikesIdAndUsersIdIsNotNullAndLikesExists_ReturnUsers() {
		Likes likes = mock(Likes.class);
		Optional<Likes> likesOptional = Optional.of((Likes) likes);
		Users usersEntity = mock(Users.class);

		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(likesOptional);

		Mockito.when(likes.getUsers()).thenReturn(usersEntity);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(_mapper.usersToGetUsersOutput(usersEntity, likes));
	}

	@Test 
	public void GetUsers_IfLikesIdAndUsersIdIsNotNullAndLikesDoesNotExist_ReturnNull() {
		Optional<Likes> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_likesRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(null);
	}

}
