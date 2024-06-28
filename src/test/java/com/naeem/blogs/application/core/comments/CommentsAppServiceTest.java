package com.naeem.blogs.application.core.comments;

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

import com.naeem.blogs.domain.core.comments.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.comments.dto.*;
import com.naeem.blogs.domain.core.comments.QComments;
import com.naeem.blogs.domain.core.comments.Comments;

import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.domain.core.users.IUsersRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CommentsAppServiceTest {

	@InjectMocks
	@Spy
	protected CommentsAppService _appService;
	@Mock
	protected ICommentsRepository _commentsRepository;
	
    @Mock
	protected IPostsRepository _postsRepository;

    @Mock
	protected IUsersRepository _usersRepository;

	@Mock
	protected ICommentsMapper _mapper;

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
	public void findCommentsById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Comments> nullOptional = Optional.ofNullable(null);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findCommentsById_IdIsNotNullAndIdExists_ReturnComments() {

		Comments comments = mock(Comments.class);
		Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.commentsToFindCommentsByIdOutput(comments));
	}
	
	
	@Test 
    public void createComments_CommentsIsNotNullAndCommentsDoesNotExist_StoreComments() { 
 
        Comments commentsEntity = mock(Comments.class); 
    	CreateCommentsInput commentsInput = new CreateCommentsInput();
		
        Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
        commentsInput.setPostId(15);
		
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
        
		
        Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
        commentsInput.setAuthorId(15);
		
        Mockito.when(_usersRepository.findById(any(Integer.class))).thenReturn(usersOptional);
        
		
        Mockito.when(_mapper.createCommentsInputToComments(any(CreateCommentsInput.class))).thenReturn(commentsEntity); 
        Mockito.when(_commentsRepository.save(any(Comments.class))).thenReturn(commentsEntity);

	   	Assertions.assertThat(_appService.create(commentsInput)).isEqualTo(_mapper.commentsToCreateCommentsOutput(commentsEntity));

    } 
	@Test
	public void createComments_CommentsIsNotNullAndCommentsDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreateCommentsInput comments = mock(CreateCommentsInput.class);
		
		Mockito.when(_mapper.createCommentsInputToComments(any(CreateCommentsInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(comments)).isEqualTo(null);
	}
	
	@Test
	public void createComments_CommentsIsNotNullAndCommentsDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreateCommentsInput comments = new CreateCommentsInput();
	    
        comments.setPostId(15);
     
     	Optional<Posts> nullOptional = Optional.ofNullable(null);
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
        
//		Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(comments)).isEqualTo(null);
    }
    
    @Test
	public void updateComments_CommentsIsNotNullAndCommentsDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdateCommentsInput commentsInput = mock(UpdateCommentsInput.class);
		Comments comments = mock(Comments.class); 
		
     	Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);
		
		Mockito.when(_mapper.updateCommentsInputToComments(any(UpdateCommentsInput.class))).thenReturn(comments); 
		Assertions.assertThat(_appService.update(ID,commentsInput)).isEqualTo(null);
	}
	
	@Test
	public void updateComments_CommentsIsNotNullAndCommentsDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdateCommentsInput commentsInput = new UpdateCommentsInput();
        commentsInput.setPostId(15);
     
    	Comments comments = mock(Comments.class);
		
     	Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);
		
		Mockito.when(_mapper.updateCommentsInputToComments(any(UpdateCommentsInput.class))).thenReturn(comments);
     	Optional<Posts> nullOptional = Optional.ofNullable(null);
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(ID,commentsInput)).isEqualTo(null);
	}

		
	@Test
	public void updateComments_CommentsIdIsNotNullAndIdExists_ReturnUpdatedComments() {

		Comments commentsEntity = mock(Comments.class);
		UpdateCommentsInput comments= mock(UpdateCommentsInput.class);
		
		Optional<Comments> commentsOptional = Optional.of((Comments) commentsEntity);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);
	 		
		Mockito.when(_mapper.updateCommentsInputToComments(any(UpdateCommentsInput.class))).thenReturn(commentsEntity);
		Mockito.when(_commentsRepository.save(any(Comments.class))).thenReturn(commentsEntity);
		Assertions.assertThat(_appService.update(ID,comments)).isEqualTo(_mapper.commentsToUpdateCommentsOutput(commentsEntity));
	}
    
	@Test
	public void deleteComments_CommentsIsNotNullAndCommentsExists_CommentsRemoved() {

		Comments comments = mock(Comments.class);
		Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);
 		
		_appService.delete(ID); 
		verify(_commentsRepository).delete(comments);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Comments> list = new ArrayList<>();
		Page<Comments> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindCommentsByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_commentsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Comments> list = new ArrayList<>();
		Comments comments = mock(Comments.class);
		list.add(comments);
    	Page<Comments> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindCommentsByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.commentsToFindCommentsByIdOutput(comments));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_commentsRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QComments comments = QComments.commentsEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("content",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(comments.content.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(comments,map,searchMap)).isEqualTo(builder);
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
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QComments comments = QComments.commentsEntity;
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
        builder.or(comments.content.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QComments.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Posts
	@Test
	public void GetPosts_IfCommentsIdAndPostsIdIsNotNullAndCommentsExists_ReturnPosts() {
		Comments comments = mock(Comments.class);
		Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Posts postsEntity = mock(Posts.class);

		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);

		Mockito.when(comments.getPosts()).thenReturn(postsEntity);
		Assertions.assertThat(_appService.getPosts(ID)).isEqualTo(_mapper.postsToGetPostsOutput(postsEntity, comments));
	}

	@Test 
	public void GetPosts_IfCommentsIdAndPostsIdIsNotNullAndCommentsDoesNotExist_ReturnNull() {
		Optional<Comments> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPosts(ID)).isEqualTo(null);
	}
   
    //Users
	@Test
	public void GetUsers_IfCommentsIdAndUsersIdIsNotNullAndCommentsExists_ReturnUsers() {
		Comments comments = mock(Comments.class);
		Optional<Comments> commentsOptional = Optional.of((Comments) comments);
		Users usersEntity = mock(Users.class);

		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(commentsOptional);

		Mockito.when(comments.getUsers()).thenReturn(usersEntity);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(_mapper.usersToGetUsersOutput(usersEntity, comments));
	}

	@Test 
	public void GetUsers_IfCommentsIdAndUsersIdIsNotNullAndCommentsDoesNotExist_ReturnNull() {
		Optional<Comments> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_commentsRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getUsers(ID)).isEqualTo(null);
	}

}
