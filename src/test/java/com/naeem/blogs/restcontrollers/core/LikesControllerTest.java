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
import com.naeem.blogs.application.core.likes.LikesAppService;
import com.naeem.blogs.application.core.likes.dto.*;
import com.naeem.blogs.domain.core.likes.ILikesRepository;
import com.naeem.blogs.domain.core.likes.Likes;

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
public class LikesControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("likesRepository") 
	protected ILikesRepository likes_repository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	

	@SpyBean
	@Qualifier("likesAppService")
	protected LikesAppService likesAppService;
	
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

	protected Likes likes;

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
		em.createNativeQuery("truncate table public.likes CASCADE").executeUpdate();
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

	public Likes createEntity() {
		Posts posts = createPostsEntity();
		Users users = createUsersEntity();
	
		Likes likesEntity = new Likes();
    	likesEntity.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-01 09:15:22"));
		likesEntity.setLikeId(1);
		likesEntity.setVersiono(0L);
		likesEntity.setPosts(posts);
		likesEntity.setUsers(users);
		
		return likesEntity;
	}
    public CreateLikesInput createLikesInput() {
	
	    CreateLikesInput likesInput = new CreateLikesInput();
    	likesInput.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-10 05:25:22"));
		
		return likesInput;
	}

	public Likes createNewEntity() {
		Likes likes = new Likes();
    	likes.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-08-11 05:35:22"));
		likes.setLikeId(3);
		
		return likes;
	}
	
	public Likes createUpdateEntity() {
		Likes likes = new Likes();
    	likes.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-09 05:45:22"));
		likes.setLikeId(4);
		
		return likes;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final LikesController likesController = new LikesController(likesAppService, postsAppService, usersAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(likesController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		likes= createEntity();
		List<Likes> list= likes_repository.findAll();
		if(!list.contains(likes)) {
			likes=likes_repository.save(likes);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/likes/" + likes.getLikeId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/likes/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateLikes_LikesDoesNotExist_ReturnStatusOk() throws Exception {
		CreateLikesInput likesInput = createLikesInput();	
		
	    
		Posts posts =  createPostsEntity();

		likesInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
	    
		Users users =  createUsersEntity();

		likesInput.setUserId(Integer.parseInt(users.getUserId().toString()));
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(likesInput);

		mvc.perform(post("/likes").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	@Test
	public void CreateLikes_postsDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateLikesInput likes = createLikesInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(likes);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/likes")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    
	
	@Test
	public void CreateLikes_usersDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateLikesInput likes = createLikesInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(likes);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/likes")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    

	@Test
	public void DeleteLikes_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(likesAppService).findById(999);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/likes/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a likes with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Likes entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Posts posts = createPostsEntity();
		entity.setPosts(posts);
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = likes_repository.save(entity);
		

		FindLikesByIdOutput output= new FindLikesByIdOutput();
		output.setLikeId(entity.getLikeId());
		
         Mockito.doReturn(output).when(likesAppService).findById(entity.getLikeId());
       
    //    Mockito.when(likesAppService.findById(entity.getLikeId())).thenReturn(output);
        
		mvc.perform(delete("/likes/" + entity.getLikeId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateLikes_LikesDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(likesAppService).findById(999);
        
        UpdateLikesInput likes = new UpdateLikesInput();
		likes.setCreatedAt(SearchUtils.stringToLocalDateTime("1996-09-28 07:15:22"));
		likes.setLikeId(999);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(likes);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/likes/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Likes with id=999 not found."));
	}    

	@Test
	public void UpdateLikes_LikesExists_ReturnStatusOk() throws Exception {
		Likes entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Posts posts = createPostsEntity();
		entity.setPosts(posts);
		Users users = createUsersEntity();
		entity.setUsers(users);
		entity = likes_repository.save(entity);
		FindLikesByIdOutput output= new FindLikesByIdOutput();
		output.setCreatedAt(entity.getCreatedAt());
		output.setLikeId(entity.getLikeId());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(likesAppService.findById(entity.getLikeId())).thenReturn(output);
        
        
		
		UpdateLikesInput likesInput = new UpdateLikesInput();
		likesInput.setLikeId(entity.getLikeId());
		
		likesInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
		likesInput.setUserId(Integer.parseInt(users.getUserId().toString()));

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(likesInput);
	
		mvc.perform(put("/likes/" + entity.getLikeId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Likes de = createUpdateEntity();
		de.setLikeId(entity.getLikeId());
		likes_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/likes?search=likeId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetPosts_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/likes/999/posts")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPosts_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/likes/" + likes.getLikeId()+ "/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/likes/999/users")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/likes/" + likes.getLikeId()+ "/users")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

