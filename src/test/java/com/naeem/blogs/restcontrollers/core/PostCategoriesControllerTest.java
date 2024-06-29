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
import com.naeem.blogs.application.core.postcategories.PostCategoriesAppService;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.domain.core.postcategories.IPostCategoriesRepository;
import com.naeem.blogs.domain.core.postcategories.PostCategories;

import com.naeem.blogs.domain.core.categories.ICategoriesRepository;
import com.naeem.blogs.domain.core.categories.Categories;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.application.core.categories.CategoriesAppService;    
import com.naeem.blogs.application.core.posts.PostsAppService;    
import com.naeem.blogs.domain.core.postcategories.PostCategoriesId;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class PostCategoriesControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("postCategoriesRepository") 
	protected IPostCategoriesRepository postCategories_repository;
	
	@Autowired
	@Qualifier("categoriesRepository") 
	protected ICategoriesRepository categoriesRepository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	

	@SpyBean
	@Qualifier("postCategoriesAppService")
	protected PostCategoriesAppService postCategoriesAppService;
	
    @SpyBean
    @Qualifier("categoriesAppService")
	protected CategoriesAppService  categoriesAppService;
	
    @SpyBean
    @Qualifier("postsAppService")
	protected PostsAppService  postsAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected PostCategories postCategories;

	protected MockMvc mvc;
	
	@Autowired
	EntityManagerFactory emf;
	
    static EntityManagerFactory emfs;
    
    static int relationCount = 10;
    static int yearCount = 1971;
    static int dayCount = 10;
	private BigDecimal bigdec = new BigDecimal(1.2);
    
	int countCategories = 10;
	
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
		em.createNativeQuery("truncate table public.post_categories CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.categories CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.users CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	
	public Categories createCategoriesEntity() {
	
		if(countCategories>60) {
			countCategories = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Categories categoriesEntity = new Categories();
		
		categoriesEntity.setCategoryId(relationCount);
  		categoriesEntity.setDescription(String.valueOf(relationCount));
  		categoriesEntity.setName(String.valueOf(relationCount));
		categoriesEntity.setVersiono(0L);
		relationCount++;
		if(!categoriesRepository.findAll().contains(categoriesEntity))
		{
			 categoriesEntity = categoriesRepository.save(categoriesEntity);
		}
		countCategories++;
	    return categoriesEntity;
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

	public PostCategories createEntity() {
		Categories categories = createCategoriesEntity();
		Posts posts = createPostsEntity();
	
		PostCategories postCategoriesEntity = new PostCategories();
		postCategoriesEntity.setCategoryId(1);
		postCategoriesEntity.setPostId(1);
		postCategoriesEntity.setVersiono(0L);
		postCategoriesEntity.setCategories(categories);
		postCategoriesEntity.setCategoryId(Integer.parseInt(categories.getCategoryId().toString()));
		postCategoriesEntity.setPosts(posts);
		postCategoriesEntity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		
		return postCategoriesEntity;
	}
    public CreatePostCategoriesInput createPostCategoriesInput() {
	
	    CreatePostCategoriesInput postCategoriesInput = new CreatePostCategoriesInput();
		postCategoriesInput.setCategoryId(5);
		postCategoriesInput.setPostId(5);
		
		return postCategoriesInput;
	}

	public PostCategories createNewEntity() {
		PostCategories postCategories = new PostCategories();
		postCategories.setCategoryId(3);
		postCategories.setPostId(3);
		
		return postCategories;
	}
	
	public PostCategories createUpdateEntity() {
		PostCategories postCategories = new PostCategories();
		postCategories.setCategoryId(4);
		postCategories.setPostId(4);
		
		return postCategories;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final PostCategoriesController postCategoriesController = new PostCategoriesController(postCategoriesAppService, categoriesAppService, postsAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(postCategoriesController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		postCategories= createEntity();
		List<PostCategories> list= postCategories_repository.findAll();
		if(!list.contains(postCategories)) {
			postCategories=postCategories_repository.save(postCategories);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/postCategories/categoryId=" + postCategories.getCategoryId()+ ",postId=" + postCategories.getPostId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/postCategories/categoryId=999,postId=999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreatePostCategories_PostCategoriesDoesNotExist_ReturnStatusOk() throws Exception {
		CreatePostCategoriesInput postCategoriesInput = createPostCategoriesInput();	
		
	    
		Categories categories =  createCategoriesEntity();

		postCategoriesInput.setCategoryId(Integer.parseInt(categories.getCategoryId().toString()));
	    
		Posts posts =  createPostsEntity();

		postCategoriesInput.setPostId(Integer.parseInt(posts.getPostId().toString()));
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(postCategoriesInput);

		mvc.perform(post("/postCategories").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	

	@Test
	public void DeletePostCategories_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(postCategoriesAppService).findById(new PostCategoriesId(999, 999));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/postCategories/categoryId=999,postId=999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a postCategories with a id=categoryId=999,postId=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	PostCategories entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Categories categories = createCategoriesEntity();
		entity.setCategoryId(Integer.parseInt(categories.getCategoryId().toString()));
		entity.setCategories(categories);
		Posts posts = createPostsEntity();
		entity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		entity.setPosts(posts);
		entity = postCategories_repository.save(entity);
		

		FindPostCategoriesByIdOutput output= new FindPostCategoriesByIdOutput();
		output.setCategoryId(entity.getCategoryId());
		output.setPostId(entity.getPostId());
		
	//    Mockito.when(postCategoriesAppService.findById(new PostCategoriesId(entity.getCategoryId(), entity.getPostId()))).thenReturn(output);
        Mockito.doReturn(output).when(postCategoriesAppService).findById(new PostCategoriesId(entity.getCategoryId(), entity.getPostId()));
        
		mvc.perform(delete("/postCategories/categoryId="+ entity.getCategoryId()+ ",postId="+ entity.getPostId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdatePostCategories_PostCategoriesDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(postCategoriesAppService).findById(new PostCategoriesId(999, 999));
        
        UpdatePostCategoriesInput postCategories = new UpdatePostCategoriesInput();
		postCategories.setCategoryId(999);
		postCategories.setPostId(999);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(postCategories);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/postCategories/categoryId=999,postId=999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. PostCategories with id=categoryId=999,postId=999 not found."));
	}    

	@Test
	public void UpdatePostCategories_PostCategoriesExists_ReturnStatusOk() throws Exception {
		PostCategories entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Categories categories = createCategoriesEntity();
		entity.setCategoryId(Integer.parseInt(categories.getCategoryId().toString()));
		entity.setCategories(categories);
		Posts posts = createPostsEntity();
		entity.setPostId(Integer.parseInt(posts.getPostId().toString()));
		entity.setPosts(posts);
		entity = postCategories_repository.save(entity);
		FindPostCategoriesByIdOutput output= new FindPostCategoriesByIdOutput();
		output.setCategoryId(entity.getCategoryId());
		output.setPostId(entity.getPostId());
		output.setVersiono(entity.getVersiono());
		
	    Mockito.when(postCategoriesAppService.findById(new PostCategoriesId(entity.getCategoryId(), entity.getPostId()))).thenReturn(output);
        
        
		
		UpdatePostCategoriesInput postCategoriesInput = new UpdatePostCategoriesInput();
		postCategoriesInput.setCategoryId(entity.getCategoryId());
		postCategoriesInput.setPostId(entity.getPostId());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(postCategoriesInput);
	
		mvc.perform(put("/postCategories/categoryId=" + entity.getCategoryId()+ ",postId=" + entity.getPostId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		PostCategories de = createUpdateEntity();
		de.setCategoryId(entity.getCategoryId());
		de.setPostId(entity.getPostId());
		postCategories_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/postCategories?search=categoryId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetCategories_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postCategories/categoryId999/categories")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=categoryId999"));
	
	}    
	@Test
	public void GetCategories_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postCategories/categoryId=999,postId=999/categories")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetCategories_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/postCategories/categoryId=" + postCategories.getCategoryId()+ ",postId=" + postCategories.getPostId()+ "/categories")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetPosts_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postCategories/categoryId999/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=categoryId999"));
	
	}    
	@Test
	public void GetPosts_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/postCategories/categoryId=999,postId=999/posts")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPosts_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/postCategories/categoryId=" + postCategories.getCategoryId()+ ",postId=" + postCategories.getPostId()+ "/posts")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

