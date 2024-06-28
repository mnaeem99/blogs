package com.naeem.blogs.restcontrollers.extended;

import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.naeem.blogs.application.extended.posttags.PostTagsAppServiceExtended;
import com.naeem.blogs.domain.extended.posttags.IPostTagsRepositoryExtended;
import com.naeem.blogs.domain.core.posttags.PostTags;
import com.naeem.blogs.domain.extended.posts.IPostsRepositoryExtended;
import com.naeem.blogs.domain.extended.tags.ITagsRepositoryExtended;
import com.naeem.blogs.domain.extended.users.IUsersRepositoryExtended;
import com.naeem.blogs.application.extended.posts.PostsAppServiceExtended;    
import com.naeem.blogs.application.extended.tags.TagsAppServiceExtended;    

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
public class PostTagsControllerTestExtended extends DatabaseContainerConfig {
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("postTagsRepositoryExtended") 
	protected IPostTagsRepositoryExtended postTags_repositoryExtended;
	
	@Autowired
	@Qualifier("postsRepositoryExtended") 
	protected IPostsRepositoryExtended postsRepositoryExtended;
	
	@Autowired
	@Qualifier("tagsRepositoryExtended") 
	protected ITagsRepositoryExtended tagsRepositoryExtended;
	
	@Autowired
	@Qualifier("usersRepositoryExtended") 
	protected IUsersRepositoryExtended usersRepositoryExtended;
	

	@SpyBean
	@Qualifier("postTagsAppServiceExtended")
	protected PostTagsAppServiceExtended postTagsAppServiceExtended;
	
    @SpyBean
    @Qualifier("postsAppServiceExtended")
	protected PostsAppServiceExtended  postsAppServiceExtended;
	
    @SpyBean
    @Qualifier("tagsAppServiceExtended")
	protected TagsAppServiceExtended  tagsAppServiceExtended;
	
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
    
		final PostTagsControllerExtended postTagsController = new PostTagsControllerExtended(postTagsAppServiceExtended, postsAppServiceExtended, tagsAppServiceExtended,
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
     //add code required for initialization 
	}
		
	//Add your custom code here	
}
