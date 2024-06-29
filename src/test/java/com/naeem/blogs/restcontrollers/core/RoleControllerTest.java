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
import com.naeem.blogs.application.core.authorization.role.RoleAppService;
import com.naeem.blogs.application.core.authorization.role.dto.*;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.role.Role;

import com.naeem.blogs.application.core.authorization.rolepermission.RolepermissionAppService;    
import com.naeem.blogs.application.core.authorization.usersrole.UsersroleAppService;    
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class RoleControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("roleRepository") 
	protected IRoleRepository role_repository;
	

	@SpyBean
	@Qualifier("roleAppService")
	protected RoleAppService roleAppService;
	
    @SpyBean
    @Qualifier("rolepermissionAppService")
	protected RolepermissionAppService  rolepermissionAppService;
	
    @SpyBean
    @Qualifier("usersroleAppService")
	protected UsersroleAppService  usersroleAppService;
	
	@SpyBean
	protected JWTAppService jwtAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Role role;

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
		em.createNativeQuery("truncate table public.role CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	

	public Role createEntity() {
	
		Role roleEntity = new Role();
		roleEntity.setDisplayName("1");
		roleEntity.setId(1L);
		roleEntity.setName("1");
		roleEntity.setVersiono(0L);
		
		return roleEntity;
	}
    public CreateRoleInput createRoleInput() {
	
	    CreateRoleInput roleInput = new CreateRoleInput();
  		roleInput.setDisplayName("5");
  		roleInput.setName("5");
		
		return roleInput;
	}

	public Role createNewEntity() {
		Role role = new Role();
		role.setDisplayName("3");
		role.setId(3L);
		role.setName("3");
		
		return role;
	}
	
	public Role createUpdateEntity() {
		Role role = new Role();
		role.setDisplayName("4");
		role.setId(4L);
		role.setName("4");
		
		return role;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final RoleController roleController = new RoleController(roleAppService, rolepermissionAppService, usersroleAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(roleController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		role= createEntity();
		List<Role> list= role_repository.findAll();
		if(!list.contains(role)) {
			role=role_repository.save(role);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/role/" + role.getId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/role/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateRole_RoleDoesNotExist_ReturnStatusOk() throws Exception {
		CreateRoleInput roleInput = createRoleInput();	
		
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(roleInput);

		mvc.perform(post("/role").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	

	@Test
	public void DeleteRole_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(roleAppService).findById(999L);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/role/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a role with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Role entity =  createNewEntity();
	 	entity.setVersiono(0L);
		entity = role_repository.save(entity);
		

		FindRoleByIdOutput output= new FindRoleByIdOutput();
		output.setDisplayName(entity.getDisplayName());
		output.setId(entity.getId());
		output.setName(entity.getName());
		
         Mockito.doReturn(output).when(roleAppService).findById(entity.getId());
       
    //    Mockito.when(roleAppService.findById(entity.getId())).thenReturn(output);
        
		mvc.perform(delete("/role/" + entity.getId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateRole_RoleDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(roleAppService).findById(999L);
        
        UpdateRoleInput role = new UpdateRoleInput();
  		role.setDisplayName("999");
		role.setId(999L);
  		role.setName("999");

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(role);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/role/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Role with id=999 not found."));
	}    

	@Test
	public void UpdateRole_RoleExists_ReturnStatusOk() throws Exception {
		Role entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		entity = role_repository.save(entity);
		FindRoleByIdOutput output= new FindRoleByIdOutput();
		output.setDisplayName(entity.getDisplayName());
		output.setId(entity.getId());
		output.setName(entity.getName());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(roleAppService.findById(entity.getId())).thenReturn(output);
        
        
		
		UpdateRoleInput roleInput = new UpdateRoleInput();
		roleInput.setDisplayName(entity.getDisplayName());
		roleInput.setId(entity.getId());
		roleInput.setName(entity.getName());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(roleInput);
	
		mvc.perform(put("/role/" + entity.getId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Role de = createUpdateEntity();
		de.setId(entity.getId());
		role_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/role?search=id[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	
	@Test
	public void GetRolepermissions_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("id", "1");
		
        Mockito.when(roleAppService.parseRolepermissionsJoinColumn("roleId")).thenReturn(joinCol);
		mvc.perform(get("/role/1/rolepermissions?search=roleId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetRolepermissions_searchIsNotEmpty() {
	
		Mockito.when(roleAppService.parseRolepermissionsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/role/1/rolepermissions?search=roleId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
	
	
	@Test
	public void GetUsersroles_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("id", "1");
		
        Mockito.when(roleAppService.parseUsersrolesJoinColumn("roleId")).thenReturn(joinCol);
		mvc.perform(get("/role/1/usersroles?search=roleId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetUsersroles_searchIsNotEmpty() {
	
		Mockito.when(roleAppService.parseUsersrolesJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/role/1/usersroles?search=roleId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
    
}

