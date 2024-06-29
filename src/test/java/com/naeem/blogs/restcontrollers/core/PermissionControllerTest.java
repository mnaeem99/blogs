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
import com.naeem.blogs.application.core.authorization.permission.PermissionAppService;
import com.naeem.blogs.application.core.authorization.permission.dto.*;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.Permission;

import com.naeem.blogs.application.core.authorization.rolepermission.RolepermissionAppService;    
import com.naeem.blogs.application.core.authorization.userspermission.UserspermissionAppService;    
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class PermissionControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("permissionRepository") 
	protected IPermissionRepository permission_repository;
	

	@SpyBean
	@Qualifier("permissionAppService")
	protected PermissionAppService permissionAppService;
	
    @SpyBean
    @Qualifier("rolepermissionAppService")
	protected RolepermissionAppService  rolepermissionAppService;
	
    @SpyBean
    @Qualifier("userspermissionAppService")
	protected UserspermissionAppService  userspermissionAppService;
	
	@SpyBean
	protected JWTAppService jwtAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Permission permission;

	protected MockMvc mvc;
	
	@Autowired
	EntityManagerFactory emf;
	
    static EntityManagerFactory emfs;
    
    static int relationCount = 10;
    static int yearCount = 1971;
    static int dayCount = 10;
	private BigDecimal bigdec = new BigDecimal(1.2);
    
	@PostConstruct
	public void init() {
	emfs = emf;
	}

	@AfterClass
	public static void cleanup() {
		EntityManager em = emfs.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("truncate table public.permission CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	

	public Permission createEntity() {
	
		Permission permissionEntity = new Permission();
		permissionEntity.setDisplayName("1");
		permissionEntity.setId(1L);
		permissionEntity.setName("1");
		permissionEntity.setVersiono(0L);
		
		return permissionEntity;
	}
    public CreatePermissionInput createPermissionInput() {
	
	    CreatePermissionInput permissionInput = new CreatePermissionInput();
  		permissionInput.setDisplayName("5");
  		permissionInput.setName("5");
		
		return permissionInput;
	}

	public Permission createNewEntity() {
		Permission permission = new Permission();
		permission.setDisplayName("3");
		permission.setId(3L);
		permission.setName("3");
		
		return permission;
	}
	
	public Permission createUpdateEntity() {
		Permission permission = new Permission();
		permission.setDisplayName("4");
		permission.setId(4L);
		permission.setName("4");
		
		return permission;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final PermissionController permissionController = new PermissionController(permissionAppService, rolepermissionAppService, userspermissionAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(permissionController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		permission= createEntity();
		List<Permission> list= permission_repository.findAll();
		if(!list.contains(permission)) {
			permission=permission_repository.save(permission);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/permission/" + permission.getId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/permission/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreatePermission_PermissionDoesNotExist_ReturnStatusOk() throws Exception {
		CreatePermissionInput permissionInput = createPermissionInput();	
		
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(permissionInput);

		mvc.perform(post("/permission").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	

	@Test
	public void DeletePermission_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(permissionAppService).findById(999L);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/permission/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a permission with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Permission entity =  createNewEntity();
	 	entity.setVersiono(0L);
		entity = permission_repository.save(entity);
		

		FindPermissionByIdOutput output= new FindPermissionByIdOutput();
		output.setDisplayName(entity.getDisplayName());
		output.setId(entity.getId());
		output.setName(entity.getName());
		
         Mockito.doReturn(output).when(permissionAppService).findById(entity.getId());
       
    //    Mockito.when(permissionAppService.findById(entity.getId())).thenReturn(output);
        
		mvc.perform(delete("/permission/" + entity.getId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdatePermission_PermissionDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(permissionAppService).findById(999L);
        
        UpdatePermissionInput permission = new UpdatePermissionInput();
  		permission.setDisplayName("999");
		permission.setId(999L);
  		permission.setName("999");

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(permission);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/permission/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Permission with id=999 not found."));
	}    

	@Test
	public void UpdatePermission_PermissionExists_ReturnStatusOk() throws Exception {
		Permission entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		entity = permission_repository.save(entity);
		FindPermissionByIdOutput output= new FindPermissionByIdOutput();
		output.setDisplayName(entity.getDisplayName());
		output.setId(entity.getId());
		output.setName(entity.getName());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(permissionAppService.findById(entity.getId())).thenReturn(output);
        
        
		
		UpdatePermissionInput permissionInput = new UpdatePermissionInput();
		permissionInput.setDisplayName(entity.getDisplayName());
		permissionInput.setId(entity.getId());
		permissionInput.setName(entity.getName());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(permissionInput);
	
		mvc.perform(put("/permission/" + entity.getId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Permission de = createUpdateEntity();
		de.setId(entity.getId());
		permission_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/permission?search=id[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	
	@Test
	public void GetRolepermissions_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("id", "1");
		
        Mockito.when(permissionAppService.parseRolepermissionsJoinColumn("permissionId")).thenReturn(joinCol);
		mvc.perform(get("/permission/1/rolepermissions?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetRolepermissions_searchIsNotEmpty() {
	
		Mockito.when(permissionAppService.parseRolepermissionsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/permission/1/rolepermissions?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	
	@Test
	public void GetUserspermissions_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("id", "1");
		
        Mockito.when(permissionAppService.parseUserspermissionsJoinColumn("permissionId")).thenReturn(joinCol);
		mvc.perform(get("/permission/1/userspermissions?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetUserspermissions_searchIsNotEmpty() {
	
		Mockito.when(permissionAppService.parseUserspermissionsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/permission/1/userspermissions?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
    
}

