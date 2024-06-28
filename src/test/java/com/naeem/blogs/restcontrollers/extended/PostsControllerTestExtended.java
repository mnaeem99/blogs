package com.naeem.blogs.restcontrollers.extended;

import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.application.extended.posts.PostsAppServiceExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.application.extended.comments.CommentsAppServiceExtended;    
import com.naeem.blogs.application.extended.likes.LikesAppServiceExtended;    
import com.naeem.blogs.application.extended.postcategories.PostCategoriesAppServiceExtended;    
import com.naeem.blogs.application.extended.posttags.PostTagsAppServiceExtended;    
import com.naeem.blogs.application.extended.users.UsersAppServiceExtended;    

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
public class PostsControllerTestExtended extends DatabaseContainerConfig {
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("postsRepositoryExtended") 
	protected IPostsRepositoryExtended posts_repositoryExtended;
	
	@Autowired
	@Qualifier("usersRepositoryExtended") 
	protected IUsersRepositoryExtended usersRepositoryExtended;
	
	@Autowired
	@Qualifier("postsRepositoryExtended") 
	protected IPostsRepositoryExtended postsRepositoryExtended;
	

	@SpyBean
	@Qualifier("postsAppServiceExtended")
	protected PostsAppServiceExtended postsAppServiceExtended;
	
    @SpyBean
    @Qualifier("commentsAppServiceExtended")
	protected CommentsAppServiceExtended  commentsAppServiceExtended;
	
    @SpyBean
    @Qualifier("likesAppServiceExtended")
	protected LikesAppServiceExtended  likesAppServiceExtended;
	
    @SpyBean
    @Qualifier("postCategoriesAppServiceExtended")
	protected PostCategoriesAppServiceExtended  postCategoriesAppServiceExtended;
	
    @SpyBean
    @Qualifier("postTagsAppServiceExtended")
	protected PostTagsAppServiceExtended  postTagsAppServiceExtended;
	
    @SpyBean
    @Qualifier("usersAppServiceExtended")
	protected UsersAppServiceExtended  usersAppServiceExtended;
	
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
    
		final PostsControllerExtended postsController = new PostsControllerExtended(postsAppServiceExtended, commentsAppServiceExtended, likesAppServiceExtended, postCategoriesAppServiceExtended, postTagsAppServiceExtended, usersAppServiceExtended,
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
     //add code required for initialization 
	}
		
	//Add your custom code here	
}
