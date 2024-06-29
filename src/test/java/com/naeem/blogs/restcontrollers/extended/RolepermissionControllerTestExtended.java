package com.naeem.blogs.restcontrollers.extended;

import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.application.extended.authorization.rolepermission.RolepermissionAppServiceExtended;
import com.naeem.blogs.domain.extended.authorization.rolepermission.IRolepermissionRepositoryExtended;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import com.naeem.blogs.domain.extended.authorization.permission.IPermissionRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.role.IRoleRepositoryExtended;
import com.naeem.blogs.application.extended.authorization.permission.PermissionAppServiceExtended;    
import com.naeem.blogs.application.extended.authorization.role.RoleAppServiceExtended;    
import com.naeem.blogs.security.JWTAppService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/*Uncomment below annotations before running tests*/
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(properties = "spring.profiles.active=test")
public class RolepermissionControllerTestExtended extends DatabaseContainerConfig {
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("rolepermissionRepositoryExtended") 
	protected IRolepermissionRepositoryExtended rolepermission_repositoryExtended;
	
	@Autowired
	@Qualifier("permissionRepositoryExtended") 
	protected IPermissionRepositoryExtended permissionRepositoryExtended;
	
	@Autowired
	@Qualifier("roleRepositoryExtended") 
	protected IRoleRepositoryExtended roleRepositoryExtended;
	

	@SpyBean
	@Qualifier("rolepermissionAppServiceExtended")
	protected RolepermissionAppServiceExtended rolepermissionAppServiceExtended;
	
    @SpyBean
    @Qualifier("permissionAppServiceExtended")
	protected PermissionAppServiceExtended  permissionAppServiceExtended;
	
    @SpyBean
    @Qualifier("roleAppServiceExtended")
	protected RoleAppServiceExtended  roleAppServiceExtended;
	
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
    
 	@PostConstruct
	public void init() {
	emfs = emf;
	}

	@AfterClass
	public static void cleanup() {
		EntityManager em = emfs.createEntityManager();
		//add your code you want to execute after class  
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final RolepermissionControllerExtended rolepermissionController = new RolepermissionControllerExtended(rolepermissionAppServiceExtended, permissionAppServiceExtended, roleAppServiceExtended,
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
     //add code required for initialization 
	}
		
	//Add your custom code here	
}
