package com.naeem.blogs.restcontrollers.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import org.springframework.core.env.Environment;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.commons.search.SearchUtils;
import com.naeem.blogs.application.core.comments.CommentsAppService;
import com.naeem.blogs.application.core.comments.dto.*;
import com.naeem.blogs.domain.core.comments.ICommentsRepository;
import com.naeem.blogs.domain.core.comments.Comments;

import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.users.IUsersRepository;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.application.core.posts.PostsAppService;    
import com.naeem.blogs.application.core.users.UsersAppService;    
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class CommentsControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("commentsRepository") 
	protected ICommentsRepository comments_repository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	

	@SpyBean
	@Qualifier("commentsAppService")
	protected CommentsAppService commentsAppService;
	
    @SpyBean
    @Qualifier("postsAppService")
	protected PostsAppService  postsAppService;
	
    @SpyBean
    @Qualifier("usersAppService")
	protected UsersAppService  usersAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Comments comments;

	protected MockMvc mvc;
	
	@Autowired
	EntityManagerFactory emf;
	
    static EntityManagerFactory emfs;
    
    static int relationCount = 10;
    static int yearCount = 1971;
    static int dayCount = 10;
	private BigDecimal bigdec = new BigDecimal(1.2);
    
	int countPosts = 10;
	
	int countUsers = 10;
	
	@PostConstruct
	public void init() {
	emfs = emf;
	}

	@AfterClass
	public static void cleanup() {
		EntityManager em = emfs.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("truncate table public.comments CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.users CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	
	public Posts createPostsEntity() {
	
		if(countPosts>60) {
			countPosts = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Posts postsEntity = new Posts();
		
  		postsEntity.setContent(String.valueOf(relationCount));
		postsEntity.setCreatedAt(SearchUtils.stringToLocalDateTime(yearCount+"-09-"+dayCount+" 05:25:22"));
		postsEntity.setPostId(relationCount);
  		postsEntity.setTitle(String.valueOf(relationCount));
		postsEntity.setUpdatedAt(SearchUtils.stringToLocalDateTime(yearCount+"-09-"+dayCount+" 05:25:22"));
		postsEntity.setVersiono(0L);
		relationCount++;
		Users users= createUsersEntity();
		postsEntity.setUsers(users);
		if(!postsRepository.findAll().contains(postsEntity))
		{
			 postsEntity = postsRepository.save(postsEntity);
		}
		countPosts++;
	    return postsEntity;
	}
	public Users createUsersEntity() {
	
		if(countUsers>60) {
			countUsers = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Users usersEntity = new Users();
		
		usersEntity.setCreatedAt(SearchUtils.stringToLocalDateTime(yearCount+"-09-"+dayCount+" 05:25:22"));
  		usersEntity.setEmail(String.valueOf(relationCount));
  		usersEntity.setPasswordHash(String.valueOf(relationCount));
		usersEntity.setUpdatedAt(SearchUtils.stringToLocalDateTime(yearCount+"-09-"+dayCount+" 05:25:22"));
		usersEntity.setUserId(relationCount);
  		usersEntity.setUsername(String.valueOf(relationCount));
		usersEntity.setVersiono(0L);
		relationCount++;
		if(!usersRepository.findAll().contains(usersEntity))
		{
			 usersEntity = usersRepository.save(usersEntity);
		}
		countUsers++;
	    return usersEntity;
	}

	public Comments createEntity() {
		Posts posts = createPostsEntity();
		Users users = createUsersEntity();
	
		Comments commentsEntity = new Comments();
		commentsEntity.setCommentId(1);
		commentsEntity.setContent("1");
    	commentsEntity.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-01 09:15:22"));
		commentsEntity.setVersiono(0L);
		commentsEntity.setPosts(posts);
		commentsEntity.setUsers(users);
		
		return commentsEntity;
	}
    public CreateCommentsInput createCommentsInput() {
	
	    CreateCommentsInput commentsInput = new CreateCommentsInput();
  		commentsInput.setContent("5");
    	commentsInput.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-10 05:25:22"));
		
		return commentsInput;
	}

	public Comments createNewEntity() {
		Comments comments = new Comments();
		comments.setCommentId(3);
		comments.setContent("3");
    	comments.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-11 05:35:22"));
		
		return comments;
	}
	
	public Comments createUpdateEntity() {
		Comments comments = new Comments();
		comments.setCommentId(4);
		comments.setContent("4");
    	comments.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-09 05:45:22"));
		
		return comments;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final CommentsController commentsController = new CommentsController(commentsAppService, postsAppService, usersAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(commentsController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		comments= createEntity();
		List<Comments> list= comments_repository.findAll();
		if(!list.contains(comments)) {
			comments=comments_repository.save(comments);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/comments/" + comments.getCommentId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/comments/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateComments_CommentsDoesNotExist_ReturnStatusOk() throws Exception {
		CreateCommentsInput commentsInput = createCommentsInput();	
		
	    
		Posts posts =  createPostsEntity();

		commentsInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
	    
		Users users =  createUsersEntity();

		commentsInput.setAuthorId(Integer.parseInt(users.getUserId().toString()));
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(commentsInput);

		mvc.perform(post("/comments").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	@Test
	public void CreateComments_postsDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateCommentsInput comments = createCommentsInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(comments);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/comments")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    
	
	@Test
	public void CreateComments_usersDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateCommentsInput comments = createCommentsInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(comments);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/comments")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    

	@Test
	public void DeleteComments_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(commentsAppService).findById(999);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/comments/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a comments with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Comments entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Posts posts = createPostsEntity();
		entity.setPosts(posts);
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = comments_repository.save(entity);
		

		FindCommentsByIdOutput output= new FindCommentsByIdOutput();
		output.setCommentId(entity.getCommentId());
		output.setContent(entity.getContent());
		
         Mockito.doReturn(output).when(commentsAppService).findById(entity.getCommentId());
       
    //    Mockito.when(commentsAppService.findById(entity.getCommentId())).thenReturn(output);
        
		mvc.perform(delete("/comments/" + entity.getCommentId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateComments_CommentsDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(commentsAppService).findById(999);
        
        UpdateCommentsInput comments = new UpdateCommentsInput();
		comments.setCommentId(999);
  		comments.setContent("999");
		comments.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-28 07:15:22"));

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(comments);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/comments/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Comments with id=999 not found."));
	}    

	@Test
	public void UpdateComments_CommentsExists_ReturnStatusOk() throws Exception {
		Comments entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Posts posts = createPostsEntity();
		entity.setPosts(posts);
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = comments_repository.save(entity);
		FindCommentsByIdOutput output= new FindCommentsByIdOutput();
		output.setCommentId(entity.getCommentId());
		output.setContent(entity.getContent());
		output.setCreatedAt(entity.getCreatedAt());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(commentsAppService.findById(entity.getCommentId())).thenReturn(output);
        
        
		
		UpdateCommentsInput commentsInput = new UpdateCommentsInput();
		commentsInput.setCommentId(entity.getCommentId());
		commentsInput.setContent(entity.getContent());
		
		commentsInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
		commentsInput.setAuthorId(Integer.parseInt(users.getUserId().toString()));

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(commentsInput);
	
		mvc.perform(put("/comments/" + entity.getCommentId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Comments de = createUpdateEntity();
		de.setCommentId(entity.getCommentId());
		comments_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/comments?search=commentId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetPosts_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/comments/999/posts")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPosts_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/comments/" + comments.getCommentId()+ "/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/comments/999/users")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/comments/" + comments.getCommentId()+ "/users")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

