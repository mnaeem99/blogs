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
import com.naeem.blogs.application.core.posts.PostsAppService;
import com.naeem.blogs.application.core.posts.dto.*;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;

import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.application.core.comments.CommentsAppService;    
import com.naeem.blogs.application.core.likes.LikesAppService;    
import com.naeem.blogs.application.core.postcategories.PostCategoriesAppService;    
import com.naeem.blogs.application.core.posttags.PostTagsAppService;    
import com.naeem.blogs.application.core.authorization.users.UsersAppService;    
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class PostsControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository posts_repository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	

	@SpyBean
	@Qualifier("postsAppService")
	protected PostsAppService postsAppService;
	
    @SpyBean
    @Qualifier("commentsAppService")
	protected CommentsAppService  commentsAppService;
	
    @SpyBean
    @Qualifier("likesAppService")
	protected LikesAppService  likesAppService;
	
    @SpyBean
    @Qualifier("postCategoriesAppService")
	protected PostCategoriesAppService  postCategoriesAppService;
	
    @SpyBean
    @Qualifier("postTagsAppService")
	protected PostTagsAppService  postTagsAppService;
	
    @SpyBean
    @Qualifier("usersAppService")
	protected UsersAppService  usersAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Posts posts;

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
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.users CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
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
		
  		usersEntity.setEmailAddress(String.valueOf(relationCount));
  		usersEntity.setFirstName(String.valueOf(relationCount));
		usersEntity.setIsActive(false);
		usersEntity.setIsEmailConfirmed(false);
  		usersEntity.setLastName(String.valueOf(relationCount));
  		usersEntity.setPassword(String.valueOf(relationCount));
  		usersEntity.setPhoneNumber(String.valueOf(relationCount));
		usersEntity.setUserId(Long.valueOf(relationCount));
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

	public Posts createEntity() {
		Users users = createUsersEntity();
	
		Posts postsEntity = new Posts();
		postsEntity.setContent("1");
    	postsEntity.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-01 09:15:22"));
		postsEntity.setPostId(1);
		postsEntity.setTitle("1");
    	postsEntity.setUpdatedAt(SearchUtils.stringToLocalDateTime("1996-09-01 09:15:22"));
		postsEntity.setVersiono(0L);
		postsEntity.setUsers(users);
		
		return postsEntity;
	}
    public CreatePostsInput createPostsInput() {
	
	    CreatePostsInput postsInput = new CreatePostsInput();
  		postsInput.setContent("5");
    	postsInput.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-10 05:25:22"));
  		postsInput.setTitle("5");
    	postsInput.setUpdatedAt(SearchUtils.stringToLocalDateTime("1996-08-10 05:25:22"));
		
		return postsInput;
	}

	public Posts createNewEntity() {
		Posts posts = new Posts();
		posts.setContent("3");
    	posts.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-11 05:35:22"));
		posts.setPostId(3);
		posts.setTitle("3");
    	posts.setUpdatedAt(SearchUtils.stringToLocalDateTime("1996-08-11 05:35:22"));
		
		return posts;
	}
	
	public Posts createUpdateEntity() {
		Posts posts = new Posts();
		posts.setContent("4");
    	posts.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-09 05:45:22"));
		posts.setPostId(4);
		posts.setTitle("4");
    	posts.setUpdatedAt(SearchUtils.stringToLocalDateTime("1996-09-09 05:45:22"));
		
		return posts;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final PostsController postsController = new PostsController(postsAppService, commentsAppService, likesAppService, postCategoriesAppService, postTagsAppService, usersAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(postsController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		posts= createEntity();
		List<Posts> list= posts_repository.findAll();
		if(!list.contains(posts)) {
			posts=posts_repository.save(posts);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/posts/" + posts.getPostId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/posts/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreatePosts_PostsDoesNotExist_ReturnStatusOk() throws Exception {
		CreatePostsInput postsInput = createPostsInput();	
		
	    
		Users users =  createUsersEntity();

		postsInput.setAuthorId(Integer.parseInt(users.getUserId().toString()));
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(postsInput);

		mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	
	
	
	
	@Test
	public void CreatePosts_usersDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreatePostsInput posts = createPostsInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(posts);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/posts")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    

	@Test
	public void DeletePosts_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(postsAppService).findById(999);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/posts/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a posts with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Posts entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = posts_repository.save(entity);
		

		FindPostsByIdOutput output= new FindPostsByIdOutput();
		output.setContent(entity.getContent());
		output.setPostId(entity.getPostId());
		output.setTitle(entity.getTitle());
		
         Mockito.doReturn(output).when(postsAppService).findById(entity.getPostId());
       
    //    Mockito.when(postsAppService.findById(entity.getPostId())).thenReturn(output);
        
		mvc.perform(delete("/posts/" + entity.getPostId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdatePosts_PostsDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(postsAppService).findById(999);
        
        UpdatePostsInput posts = new UpdatePostsInput();
  		posts.setContent("999");
		posts.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-28 07:15:22"));
		posts.setPostId(999);
  		posts.setTitle("999");
		posts.setUpdatedAt(SearchUtils.stringToLocalDateTime("1996-09-28 07:15:22"));

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(posts);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/posts/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Posts with id=999 not found."));
	}    

	@Test
	public void UpdatePosts_PostsExists_ReturnStatusOk() throws Exception {
		Posts entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = posts_repository.save(entity);
		FindPostsByIdOutput output= new FindPostsByIdOutput();
		output.setContent(entity.getContent());
		output.setCreatedAt(entity.getCreatedAt());
		output.setPostId(entity.getPostId());
		output.setTitle(entity.getTitle());
		output.setUpdatedAt(entity.getUpdatedAt());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(postsAppService.findById(entity.getPostId())).thenReturn(output);
        
        
		
		UpdatePostsInput postsInput = new UpdatePostsInput();
		postsInput.setContent(entity.getContent());
		postsInput.setPostId(entity.getPostId());
		postsInput.setTitle(entity.getTitle());
		
		postsInput.setAuthorId(Integer.parseInt(users.getUserId().toString()));

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(postsInput);
	
		mvc.perform(put("/posts/" + entity.getPostId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Posts de = createUpdateEntity();
		de.setPostId(entity.getPostId());
		posts_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/posts?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	
	@Test
	public void GetComments_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("postId", "1");
		
        Mockito.when(postsAppService.parseCommentsJoinColumn("postId")).thenReturn(joinCol);
		mvc.perform(get("/posts/1/comments?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetComments_searchIsNotEmpty() {
	
		Mockito.when(postsAppService.parseCommentsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/posts/1/comments?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	
	@Test
	public void GetLikes_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("postId", "1");
		
        Mockito.when(postsAppService.parseLikesJoinColumn("postId")).thenReturn(joinCol);
		mvc.perform(get("/posts/1/likes?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetLikes_searchIsNotEmpty() {
	
		Mockito.when(postsAppService.parseLikesJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/posts/1/likes?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	
	@Test
	public void GetPostCategories_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("postId", "1");
		
        Mockito.when(postsAppService.parsePostCategoriesJoinColumn("postId")).thenReturn(joinCol);
		mvc.perform(get("/posts/1/postCategories?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetPostCategories_searchIsNotEmpty() {
	
		Mockito.when(postsAppService.parsePostCategoriesJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/posts/1/postCategories?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	
	@Test
	public void GetPostTags_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("postId", "1");
		
        Mockito.when(postsAppService.parsePostTagsJoinColumn("postId")).thenReturn(joinCol);
		mvc.perform(get("/posts/1/postTags?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetPostTags_searchIsNotEmpty() {
	
		Mockito.when(postsAppService.parsePostTagsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/posts/1/postTags?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	@Test
	public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/posts/999/users")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/posts/" + posts.getPostId()+ "/users")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

