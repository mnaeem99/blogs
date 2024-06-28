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
import com.naeem.blogs.application.core.posttags.PostTagsAppService;
import com.naeem.blogs.application.core.posttags.dto.*;
import com.naeem.blogs.domain.core.posttags.IPostTagsRepository;
import com.naeem.blogs.domain.core.posttags.PostTags;

import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.tags.ITagsRepository;
import com.naeem.blogs.domain.core.tags.Tags;
import com.naeem.blogs.domain.core.users.IUsersRepository;
import com.naeem.blogs.domain.core.users.Users;
import com.naeem.blogs.application.core.posts.PostsAppService;    
import com.naeem.blogs.application.core.tags.TagsAppService;    
import com.naeem.blogs.domain.core.posttags.PostTagsId;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class PostTagsControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("postTagsRepository") 
	protected IPostTagsRepository postTags_repository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	
	@Autowired
	@Qualifier("tagsRepository") 
	protected ITagsRepository tagsRepository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	

	@SpyBean
	@Qualifier("postTagsAppService")
	protected PostTagsAppService postTagsAppService;
	
    @SpyBean
    @Qualifier("postsAppService")
	protected PostsAppService  postsAppService;
	
    @SpyBean
    @Qualifier("tagsAppService")
	protected TagsAppService  tagsAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected PostTags postTags;

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
	
	int countTags = 10;
	
	@PostConstruct
	public void init() {
	emfs = emf;
	}

	@AfterClass
	public static void cleanup() {
		EntityManager em = emfs.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("truncate table public.post_tags CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.tags CASCADE").executeUpdate();
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
	public Tags createTagsEntity() {
	
		if(countTags>60) {
			countTags = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Tags tagsEntity = new Tags();
		
  		tagsEntity.setName(String.valueOf(relationCount));
		tagsEntity.setTagId(relationCount);
		tagsEntity.setVersiono(0L);
		relationCount++;
		if(!tagsRepository.findAll().contains(tagsEntity))
		{
			 tagsEntity = tagsRepository.save(tagsEntity);
		}
		countTags++;
	    return tagsEntity;
	}

	public PostTags createEntity() {
		Posts posts = createPostsEntity();
		Tags tags = createTagsEntity();
	
		PostTags postTagsEntity = new PostTags();
		postTagsEntity.setPostId(1);
		postTagsEntity.setTagId(1);
		postTagsEntity.setVersiono(0L);
		postTagsEntity.setPosts(posts);
		postTagsEntity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		postTagsEntity.setTags(tags);
		postTagsEntity.setTagId(Integer.parseInt(tags.getTagId().toString()));
		
		return postTagsEntity;
	}
    public CreatePostTagsInput createPostTagsInput() {
	
	    CreatePostTagsInput postTagsInput = new CreatePostTagsInput();
		postTagsInput.setPostId(5);
		postTagsInput.setTagId(5);
		
		return postTagsInput;
	}

	public PostTags createNewEntity() {
		PostTags postTags = new PostTags();
		postTags.setPostId(3);
		postTags.setTagId(3);
		
		return postTags;
	}
	
	public PostTags createUpdateEntity() {
		PostTags postTags = new PostTags();
		postTags.setPostId(4);
		postTags.setTagId(4);
		
		return postTags;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final PostTagsController postTagsController = new PostTagsController(postTagsAppService, postsAppService, tagsAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(postTagsController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		postTags= createEntity();
		List<PostTags> list= postTags_repository.findAll();
		if(!list.contains(postTags)) {
			postTags=postTags_repository.save(postTags);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/postTags/postId=" + postTags.getPostId()+ ",tagId=" + postTags.getTagId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/postTags/postId=999,tagId=999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreatePostTags_PostTagsDoesNotExist_ReturnStatusOk() throws Exception {
		CreatePostTagsInput postTagsInput = createPostTagsInput();	
		
	    
		Posts posts =  createPostsEntity();

		postTagsInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
	    
		Tags tags =  createTagsEntity();

		postTagsInput.setTagId(Integer.parseInt(tags.getTagId().toString()));
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(postTagsInput);

		mvc.perform(post("/postTags").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	

	@Test
	public void DeletePostTags_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(postTagsAppService).findById(new PostTagsId(999, 999));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/postTags/postId=999,tagId=999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a postTags with a id=postId=999,tagId=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	PostTags entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Posts posts = createPostsEntity();
		entity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		entity.setPosts(posts);
		Tags tags = createTagsEntity();
		entity.setTagId(Integer.parseInt(tags.getTagId().toString()));
		entity.setTags(tags);
		entity = postTags_repository.save(entity);
		

		FindPostTagsByIdOutput output= new FindPostTagsByIdOutput();
		output.setPostId(entity.getPostId());
		output.setTagId(entity.getTagId());
		
	//    Mockito.when(postTagsAppService.findById(new PostTagsId(entity.getPostId(), entity.getTagId()))).thenReturn(output);
        Mockito.doReturn(output).when(postTagsAppService).findById(new PostTagsId(entity.getPostId(), entity.getTagId()));
        
		mvc.perform(delete("/postTags/postId="+ entity.getPostId()+ ",tagId="+ entity.getTagId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdatePostTags_PostTagsDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(postTagsAppService).findById(new PostTagsId(999, 999));
        
        UpdatePostTagsInput postTags = new UpdatePostTagsInput();
		postTags.setPostId(999);
		postTags.setTagId(999);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(postTags);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/postTags/postId=999,tagId=999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. PostTags with id=postId=999,tagId=999 not found."));
	}    

	@Test
	public void UpdatePostTags_PostTagsExists_ReturnStatusOk() throws Exception {
		PostTags entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Posts posts = createPostsEntity();
		entity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		entity.setPosts(posts);
		Tags tags = createTagsEntity();
		entity.setTagId(Integer.parseInt(tags.getTagId().toString()));
		entity.setTags(tags);
		entity = postTags_repository.save(entity);
		FindPostTagsByIdOutput output= new FindPostTagsByIdOutput();
		output.setPostId(entity.getPostId());
		output.setTagId(entity.getTagId());
		output.setVersiono(entity.getVersiono());
		
	    Mockito.when(postTagsAppService.findById(new PostTagsId(entity.getPostId(), entity.getTagId()))).thenReturn(output);
        
        
		
		UpdatePostTagsInput postTagsInput = new UpdatePostTagsInput();
		postTagsInput.setPostId(entity.getPostId());
		postTagsInput.setTagId(entity.getTagId());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(postTagsInput);
	
		mvc.perform(put("/postTags/postId=" + entity.getPostId()+ ",tagId=" + entity.getTagId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		PostTags de = createUpdateEntity();
		de.setPostId(entity.getPostId());
		de.setTagId(entity.getTagId());
		postTags_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/postTags?search=postId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetPosts_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postTags/postId999/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=postId999"));
	
	}    
	@Test
	public void GetPosts_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postTags/postId=999,tagId=999/posts")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPosts_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/postTags/postId=" + postTags.getPostId()+ ",tagId=" + postTags.getTagId()+ "/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetTags_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postTags/postId999/tags")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=postId999"));
	
	}    
	@Test
	public void GetTags_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postTags/postId=999,tagId=999/tags")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetTags_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/postTags/postId=" + postTags.getPostId()+ ",tagId=" + postTags.getTagId()+ "/tags")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

