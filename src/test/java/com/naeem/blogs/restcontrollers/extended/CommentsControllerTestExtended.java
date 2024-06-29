package com.naeem.blogs.restcontrollers.extended;

import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.application.extended.comments.CommentsAppServiceExtended;
import com.naeem.blogs.domain.extended.comments.ICommentsRepositoryExtended;
import com.naeem.blogs.domain.core.comments.Comments;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.authorization.users.IUsersRepositoryExtended;
import com.naeem.blogs.application.extended.posts.PostsAppServiceExtended;    
import com.naeem.blogs.application.extended.authorization.users.UsersAppServiceExtended;    

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
public class CommentsControllerTestExtended extends DatabaseContainerConfig {
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("commentsRepositoryExtended") 
	protected ICommentsRepositoryExtended comments_repositoryExtended;
	
	@Autowired
	@Qualifier("postsRepositoryExtended") 
	protected IPostsRepositoryExtended postsRepositoryExtended;
	
	@Autowired
	@Qualifier("usersRepositoryExtended") 
	protected IUsersRepositoryExtended usersRepositoryExtended;
	

	@SpyBean
	@Qualifier("commentsAppServiceExtended")
	protected CommentsAppServiceExtended commentsAppServiceExtended;
	
    @SpyBean
    @Qualifier("postsAppServiceExtended")
	protected PostsAppServiceExtended  postsAppServiceExtended;
	
    @SpyBean
    @Qualifier("usersAppServiceExtended")
	protected UsersAppServiceExtended  usersAppServiceExtended;
	
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
    
		final CommentsControllerExtended commentsController = new CommentsControllerExtended(commentsAppServiceExtended, postsAppServiceExtended, usersAppServiceExtended,
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
     //add code required for initialization 
	}
		
	//Add your custom code here	
}
