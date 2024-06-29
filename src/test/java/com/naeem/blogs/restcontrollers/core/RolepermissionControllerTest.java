package com.naeem.blogs.restcontrollers.core;

import static org.mockito.ArgumentMatchers.anyLong;
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
import com.naeem.blogs.application.core.authorization.rolepermission.RolepermissionAppService;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.*;
import com.naeem.blogs.domain.core.authorization.rolepermission.IRolepermissionRepository;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;

import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.application.core.authorization.permission.PermissionAppService;    
import com.naeem.blogs.application.core.authorization.role.RoleAppService;    
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.domain.core.authorization.rolepermission.RolepermissionId;
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class RolepermissionControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("rolepermissionRepository") 
	protected IRolepermissionRepository rolepermission_repository;
	
	@Autowired
	@Qualifier("permissionRepository") 
	protected IPermissionRepository permissionRepository;
	
	@Autowired
	@Qualifier("roleRepository") 
	protected IRoleRepository roleRepository;
	

	@SpyBean
	@Qualifier("rolepermissionAppService")
	protected RolepermissionAppService rolepermissionAppService;
	
    @SpyBean
    @Qualifier("permissionAppService")
	protected PermissionAppService  permissionAppService;
	
    @SpyBean
    @Qualifier("roleAppService")
	protected RoleAppService  roleAppService;
	
	@SpyBean
	protected JWTAppService jwtAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Rolepermission rolepermission;

	protected MockMvc mvc;
	
	@Autowired
	EntityManagerFactory emf;
	
    static EntityManagerFactory emfs;
    
    static int relationCount = 10;
    static int yearCount = 1971;
    static int dayCount = 10;
	private BigDecimal bigdec = new BigDecimal(1.2);
    
	int countRole = 10;
	
	int countPermission = 10;
	
	@PostConstruct
	public void init() {
	emfs = emf;
	}

	@AfterClass
	public static void cleanup() {
		EntityManager em = emfs.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("truncate table public.rolepermission CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.permission CASCADE").executeUpdate();
		em.createNativeQuery("truncate table public.role CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	
	public Role createRoleEntity() {
	
		if(countRole>60) {
			countRole = 10;
		}

		if(dayCount>=31) {
			dayCount = 10;
			yearCount++;
		}
		
		Role roleEntity = new Role();
		
  		roleEntity.setDisplayName(String.valueOf(relationCount));
		roleEntity.setId(Long.valueOf(relationCount));
  		roleEntity.setName(String.valueOf(relationCount));
		roleEntity.setVersiono(0L);
		relationCount++;
		if(!roleRepository.findAll().contains(roleEntity))
		{
			 roleEntity = roleRepository.save(roleEntity);
		}
		countRole++;
	    return roleEntity;
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

	public Rolepermission createEntity() {
		Permission permission = createPermissionEntity();
		Role role = createRoleEntity();
	
		Rolepermission rolepermissionEntity = new Rolepermission();
		rolepermissionEntity.setPermissionId(1L);
		rolepermissionEntity.setRoleId(1L);
		rolepermissionEntity.setVersiono(0L);
		rolepermissionEntity.setPermission(permission);
		rolepermissionEntity.setPermissionId(Long.parseLong(permission.getId().toString()));
		rolepermissionEntity.setRole(role);
		rolepermissionEntity.setRoleId(Long.parseLong(role.getId().toString()));
		
		return rolepermissionEntity;
	}
    public CreateRolepermissionInput createRolepermissionInput() {
	
	    CreateRolepermissionInput rolepermissionInput = new CreateRolepermissionInput();
		rolepermissionInput.setPermissionId(5L);
		rolepermissionInput.setRoleId(5L);
		
		return rolepermissionInput;
	}

	public Rolepermission createNewEntity() {
		Rolepermission rolepermission = new Rolepermission();
		rolepermission.setPermissionId(3L);
		rolepermission.setRoleId(3L);
		
		return rolepermission;
	}
	
	public Rolepermission createUpdateEntity() {
		Rolepermission rolepermission = new Rolepermission();
		rolepermission.setPermissionId(4L);
		rolepermission.setRoleId(4L);
		
		return rolepermission;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final RolepermissionController rolepermissionController = new RolepermissionController(rolepermissionAppService, permissionAppService, roleAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(rolepermissionController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		rolepermission= createEntity();
		List<Rolepermission> list= rolepermission_repository.findAll();
		if(!list.contains(rolepermission)) {
			rolepermission=rolepermission_repository.save(rolepermission);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/rolepermission/permissionId=" + rolepermission.getPermissionId()+ ",roleId=" + rolepermission.getRoleId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/rolepermission/permissionId=999,roleId=999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateRolepermission_RolepermissionDoesNotExist_ReturnStatusOk() throws Exception {
		CreateRolepermissionInput rolepermissionInput = createRolepermissionInput();	
		
	    
		Permission permission =  createPermissionEntity();

		rolepermissionInput.setPermissionId(Long.parseLong(permission.getId().toString()));
	    
		Role role =  createRoleEntity();

		rolepermissionInput.setRoleId(Long.parseLong(role.getId().toString()));
		doNothing().when(rolepermissionAppService).deleteUserTokens(anyLong());
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(rolepermissionInput);

		mvc.perform(post("/rolepermission").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	
	@Test
	public void CreateRolepermission_permissionDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateRolepermissionInput rolepermission = createRolepermissionInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(rolepermission);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/rolepermission")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    
	
	@Test
	public void CreateRolepermission_roleDoesNotExists_ThrowEntityNotFoundException() throws Exception{

		CreateRolepermissionInput rolepermission = createRolepermissionInput();
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(rolepermission);

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
		mvc.perform(post("/rolepermission")
				.contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isNotFound()));

	}    

	@Test
	public void DeleteRolepermission_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(rolepermissionAppService).findById(new RolepermissionId(999L, 999L));
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/rolepermission/permissionId=999,roleId=999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a rolepermission with a id=permissionId=999,roleId=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Rolepermission entity =  createNewEntity();
	 	entity.setVersiono(0L);
		Permission permission = createPermissionEntity();
		entity.setPermissionId(Long.parseLong(permission.getId().toString()));
		entity.setPermission(permission);
		Role role = createRoleEntity();
		entity.setRoleId(Long.parseLong(role.getId().toString()));
		entity.setRole(role);
		entity = rolepermission_repository.save(entity);
		

		FindRolepermissionByIdOutput output= new FindRolepermissionByIdOutput();
		output.setPermissionId(entity.getPermissionId());
		output.setRoleId(entity.getRoleId());
		
	//    Mockito.when(rolepermissionAppService.findById(new RolepermissionId(entity.getPermissionId(), entity.getRoleId()))).thenReturn(output);
        Mockito.doReturn(output).when(rolepermissionAppService).findById(new RolepermissionId(entity.getPermissionId(), entity.getRoleId()));
        
		doNothing().when(rolepermissionAppService).deleteUserTokens(anyLong());
		mvc.perform(delete("/rolepermission/permissionId="+ entity.getPermissionId()+ ",roleId="+ entity.getRoleId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateRolepermission_RolepermissionDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(rolepermissionAppService).findById(new RolepermissionId(999L, 999L));
        
        UpdateRolepermissionInput rolepermission = new UpdateRolepermissionInput();
		rolepermission.setPermissionId(999L);
		rolepermission.setRoleId(999L);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(rolepermission);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/rolepermission/permissionId=999,roleId=999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Rolepermission with id=permissionId=999,roleId=999 not found."));
	}    

	@Test
	public void UpdateRolepermission_RolepermissionExists_ReturnStatusOk() throws Exception {
		Rolepermission entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		Permission permission = createPermissionEntity();
		entity.setPermissionId(Long.parseLong(permission.getId().toString()));
		entity.setPermission(permission);
		Role role = createRoleEntity();
		entity.setRoleId(Long.parseLong(role.getId().toString()));
		entity.setRole(role);
		entity = rolepermission_repository.save(entity);
		FindRolepermissionByIdOutput output= new FindRolepermissionByIdOutput();
		output.setPermissionId(entity.getPermissionId());
		output.setRoleId(entity.getRoleId());
		output.setVersiono(entity.getVersiono());
		
	    Mockito.when(rolepermissionAppService.findById(new RolepermissionId(entity.getPermissionId(), entity.getRoleId()))).thenReturn(output);
        
        
		doNothing().when(rolepermissionAppService).deleteUserTokens(anyLong());
		
		UpdateRolepermissionInput rolepermissionInput = new UpdateRolepermissionInput();
		rolepermissionInput.setPermissionId(entity.getPermissionId());
		rolepermissionInput.setRoleId(entity.getRoleId());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(rolepermissionInput);
	
		mvc.perform(put("/rolepermission/permissionId=" + entity.getPermissionId()+ ",roleId=" + entity.getRoleId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Rolepermission de = createUpdateEntity();
		de.setPermissionId(entity.getPermissionId());
		de.setRoleId(entity.getRoleId());
		rolepermission_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/rolepermission?search=permissionId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	@Test
	public void GetPermission_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/rolepermission/permissionId999/permission")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
	
	}    
	@Test
	public void GetPermission_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/rolepermission/permissionId=999,roleId=999/permission")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetPermission_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/rolepermission/permissionId=" + rolepermission.getPermissionId()+ ",roleId=" + rolepermission.getRoleId()+ "/permission")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	
	@Test
	public void GetRole_IdIsNotEmptyAndIdIsNotValid_ThrowException() {
		
		org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/rolepermission/permissionId999/role")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid id=permissionId999"));
	
	}    
	@Test
	public void GetRole_IdIsNotEmptyAndIdDoesNotExist_ReturnNotFound() {
  
	   org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(get("/rolepermission/permissionId=999,roleId=999/role")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));
	
	}    
	
	@Test
	public void GetRole_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {
	
	   mvc.perform(get("/rolepermission/permissionId=" + rolepermission.getPermissionId()+ ",roleId=" + rolepermission.getRoleId()+ "/role")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
    
}

