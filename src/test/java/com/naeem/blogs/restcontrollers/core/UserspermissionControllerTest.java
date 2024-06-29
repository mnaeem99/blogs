package com.naeem.blogs.restcontrollers.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
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
import com.naeem.blogs.application.core.authorization.userspermission.UserspermissionAppService;
import com.naeem.blogs.application.core.authorization.userspermission.dto.*;
import com.naeem.blogs.domain.core.authorization.userspermission.IUserspermissionRepository;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;

import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.application.core.authorization.permission.PermissionAppService;    
import com.naeem.blogs.application.core.authorization.users.UsersAppService;    
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.domain.core.authorization.userspermission.UserspermissionId;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class UserspermissionControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("userspermissionRepository") 
	protected IUserspermissionRepository userspermission_repository;
	
	@Autowired
	@Qualifier("permissionRepository") 
	protected IPermissionRepository permissionRepository;
	
	@Autowired
	@Qualifier("usersRepository") 
	protected IUsersRepository usersRepository;
	
	@Autowired
	@Qualifier("postsRepository") 
	protected IPostsRepository postsRepository;
	

	@SpyBean
	@Qualifier("userspermissionAppService")
	protected UserspermissionAppService userspermissionAppService;
	
    @SpyBean
    @Qualifier("permissionAppService")
	protected PermissionAppService  permissionAppService;
	
    @SpyBean
    @Qualifier("usersAppService")
	protected UsersAppService  usersAppService;
	
	@SpyBean
	protected JWTAppService jwtAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Userspermission userspermission;

	protected MockMvc mvc;
	
	@Autowired
	EntityManagerFactory emf;
	
    static EntityManagerFactory emfs;
    
    static int relationCount = 10;
    static int yearCount = 1971;
    static int dayCount = 10;
	private BigDecimal bigdec = new BigDecimal(1.2);
    
	int countPermission = 10;
	
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
		em.createNativeQuery("truncate table public.userspermission CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.permission CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.users CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.posts CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	
	public Permission createPermissionEntity() {
	
		if(countPermission>60) {
			countPermission = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Permission permissionEntity = new Permission();
		
  		permissionEntity.setDisplayName(String.valueOf(relationCount));
		permissionEntity.setId(Long.valueOf(relationCount));
  		permissionEntity.setName(String.valueOf(relationCount));
		permissionEntity.setVersiono(0L);
		relationCount++;
		if(!permissionRepository.findAll().contains(permissionEntity))
		{
			 permissionEntity = permissionRepository.save(permissionEntity);
		}
		countPermission++;
	    return permissionEntity;
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
		
        usersEntity.setEmailAddress("bbc"+countUsers+"@d.c");
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

	public Userspermission createEntity() {
		Permission permission = createPermissionEntity();
		Users users = createUsersEntity();
	
		Userspermission userspermissionEntity = new Userspermission();
		userspermissionEntity.setPermissionId(1L);
		userspermissionEntity.setRevoked(false);
		userspermissionEntity.setUsersUserId(1L);
		userspermissionEntity.setVersiono(0L);
		userspermissionEntity.setPermission(permission);
		userspermissionEntity.setPermissionId(Long.parseLong(permission.getId().toString()));
		userspermissionEntity.setUsers(users);
		userspermissionEntity.setUsersUserId(Long.parseLong(users.getUserId().toString()));
		
		return userspermissionEntity;
	}
    public CreateUserspermissionInput createUserspermissionInput() {
	
	    CreateUserspermissionInput userspermissionInput = new CreateUserspermissionInput();
		userspermissionInput.setPermissionId(5L);
		userspermissionInput.setRevoked(false);
		userspermissionInput.setUsersUserId(5L);
		
		return userspermissionInput;
	}

	public Userspermission createNewEntity() {
		Userspermission userspermission = new Userspermission();
		userspermission.setPermissionId(3L);
		userspermission.setRevoked(false);
		userspermission.setUsersUserId(3L);
		
		return userspermission;
	}
	
	public Userspermission createUpdateEntity() {
		Userspermission userspermission = new Userspermission();
		userspermission.setPermissionId(4L);
		userspermission.setRevoked(false);
		userspermission.setUsersUserId(4L);
		
		return userspermission;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final UserspermissionController userspermissionController = new UserspermissionController(userspermissionAppService, permissionAppService, usersAppService,
		jwtAppService,logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(userspermissionController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		userspermission= createEntity();
		List<Userspermission> list= userspermission_repository.findAll();
		if(!list.contains(userspermission)) {
			userspermission=userspermission_repository.save(userspermission);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/userspermission/permissionId=" + userspermission.getPermissionId()+ ",usersUserId=" + userspermission.getUsersUserId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/userspermission/permissionId=999,usersUserId=999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateUserspermission_UserspermissionDoesNotExist_ReturnStatusOk() throws Exception {
		CreateUserspermissionInput userspermissionInput = createUserspermissionInput();	
		
	    
		Permission permission =  createPermissionEntity();

		userspermissionInput.setPermissionId(Long.parseLong(permission.getId().toString()));
	    
		Users users =  createUsersEntity();

		userspermissionInput.setUsersUserId(Long.parseLong(users.getUserId().toString()));
		doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));  
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(userspermissionInput);

		mvc.perform(post("/userspermission").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	@Test
	public void CreateUserspermission_permissionDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateUserspermissionInput userspermission = createUserspermissionInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(userspermission);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/userspermission")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    
	
	@Test
	public void CreateUserspermission_usersDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateUserspermissionInput userspermission = createUserspermissionInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(userspermission);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/userspermission")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    

	@Test
	public void DeleteUserspermission_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(userspermissionAppService).findById(new UserspermissionId(999L, 999L));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/userspermission/permissionId=999,usersUserId=999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a userspermission with a id=permissionId=999,usersUserId=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Userspermission entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Permission permission = createPermissionEntity();
		entity.setPermissionId(Long.parseLong(permission.getId().toString()));
		entity.setPermission(permission);
		Users users = createUsersEntity();
		entity.setUsersUserId(Long.parseLong(users.getUserId().toString()));
		entity.setUsers(users);
		entity = userspermission_repository.save(entity);
		

		FindUserspermissionByIdOutput output= new FindUserspermissionByIdOutput();
		output.setPermissionId(entity.getPermissionId());
		output.setUsersUserId(entity.getUsersUserId());
		
	//    Mockito.when(userspermissionAppService.findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersUserId()))).thenReturn(output);
        Mockito.doReturn(output).when(userspermissionAppService).findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersUserId()));
        
		doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));  
		mvc.perform(delete("/userspermission/permissionId="+ entity.getPermissionId()+ ",usersUserId="+ entity.getUsersUserId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateUserspermission_UserspermissionDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(userspermissionAppService).findById(new UserspermissionId(999L, 999L));
        
        UpdateUserspermissionInput userspermission = new UpdateUserspermissionInput();
		userspermission.setPermissionId(999L);
		userspermission.setRevoked(false);
		userspermission.setUsersUserId(999L);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(userspermission);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/userspermission/permissionId=999,usersUserId=999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Userspermission with id=permissionId=999,usersUserId=999 not found."));
	}    

	@Test
	public void UpdateUserspermission_UserspermissionExists_ReturnStatusOk() throws Exception {
		Userspermission entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Permission permission = createPermissionEntity();
		entity.setPermissionId(Long.parseLong(permission.getId().toString()));
		entity.setPermission(permission);
		Users users = createUsersEntity();
		entity.setUsersUserId(Long.parseLong(users.getUserId().toString()));
		entity.setUsers(users);
		entity = userspermission_repository.save(entity);
		FindUserspermissionByIdOutput output= new FindUserspermissionByIdOutput();
		output.setPermissionId(entity.getPermissionId());
		output.setRevoked(entity.getRevoked());
		output.setUsersUserId(entity.getUsersUserId());
		output.setVersiono(entity.getVersiono());
		
	    Mockito.when(userspermissionAppService.findById(new UserspermissionId(entity.getPermissionId(), entity.getUsersUserId()))).thenReturn(output);
        
        
		doNothing().when(jwtAppService).deleteAllUserTokens(any(String.class));  
		
		UpdateUserspermissionInput userspermissionInput = new UpdateUserspermissionInput();
		userspermissionInput.setPermissionId(entity.getPermissionId());
		userspermissionInput.setUsersUserId(entity.getUsersUserId());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(userspermissionInput);
	
		mvc.perform(put("/userspermission/permissionId=" + entity.getPermissionId()+ ",usersUserId=" + entity.getUsersUserId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Userspermission de = createUpdateEntity();
		de.setPermissionId(entity.getPermissionId());
		de.setUsersUserId(entity.getUsersUserId());
		userspermission_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/userspermission?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetPermission_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/userspermission/permissionId999/permission")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
	
	}    
	@Test
	public void GetPermission_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/userspermission/permissionId=999,usersUserId=999/permission")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPermission_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/userspermission/permissionId=" + userspermission.getPermissionId()+ ",usersUserId=" + userspermission.getUsersUserId()+ "/permission")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetUsers_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/userspermission/permissionId999/users")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
	
	}    
	@Test
	public void GetUsers_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/userspermission/permissionId=999,usersUserId=999/users")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetUsers_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/userspermission/permissionId=" + userspermission.getPermissionId()+ ",usersUserId=" + userspermission.getUsersUserId()+ "/users")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

