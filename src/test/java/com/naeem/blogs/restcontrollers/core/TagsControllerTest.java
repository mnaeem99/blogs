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
import com.naeem.blogs.application.core.tags.TagsAppService;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.domain.core.tags.ITagsRepository;
import com.naeem.blogs.domain.core.tags.Tags;

import com.naeem.blogs.application.core.posttags.PostTagsAppService;    
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class TagsControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("tagsRepository") 
	protected ITagsRepository tags_repository;
	

	@SpyBean
	@Qualifier("tagsAppService")
	protected TagsAppService tagsAppService;
	
    @SpyBean
    @Qualifier("postTagsAppService")
	protected PostTagsAppService  postTagsAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Tags tags;

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
		em.createNativeQuery("truncate table public.tags CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	

	public Tags createEntity() {
	
		Tags tagsEntity = new Tags();
		tagsEntity.setName("1");
		tagsEntity.setTagId(1);
		tagsEntity.setVersiono(0L);
		
		return tagsEntity;
	}
    public CreateTagsInput createTagsInput() {
	
	    CreateTagsInput tagsInput = new CreateTagsInput();
  		tagsInput.setName("5");
		
		return tagsInput;
	}

	public Tags createNewEntity() {
		Tags tags = new Tags();
		tags.setName("3");
		tags.setTagId(3);
		
		return tags;
	}
	
	public Tags createUpdateEntity() {
		Tags tags = new Tags();
		tags.setName("4");
		tags.setTagId(4);
		
		return tags;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final TagsController tagsController = new TagsController(tagsAppService, postTagsAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(tagsController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		tags= createEntity();
		List<Tags> list= tags_repository.findAll();
		if(!list.contains(tags)) {
			tags=tags_repository.save(tags);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/tags/" + tags.getTagId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/tags/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateTags_TagsDoesNotExist_ReturnStatusOk() throws Exception {
		CreateTagsInput tagsInput = createTagsInput();	
		
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(tagsInput);

		mvc.perform(post("/tags").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	

	@Test
	public void DeleteTags_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(tagsAppService).findById(999);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/tags/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a tags with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Tags entity =  createNewEntity();
	 	entity.setVersiono(0L);
		entity = tags_repository.save(entity);
		

		FindTagsByIdOutput output= new FindTagsByIdOutput();
		output.setName(entity.getName());
		output.setTagId(entity.getTagId());
		
         Mockito.doReturn(output).when(tagsAppService).findById(entity.getTagId());
       
    //    Mockito.when(tagsAppService.findById(entity.getTagId())).thenReturn(output);
        
		mvc.perform(delete("/tags/" + entity.getTagId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateTags_TagsDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(tagsAppService).findById(999);
        
        UpdateTagsInput tags = new UpdateTagsInput();
  		tags.setName("999");
		tags.setTagId(999);

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(tags);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/tags/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Tags with id=999 not found."));
	}    

	@Test
	public void UpdateTags_TagsExists_ReturnStatusOk() throws Exception {
		Tags entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		entity = tags_repository.save(entity);
		FindTagsByIdOutput output= new FindTagsByIdOutput();
		output.setName(entity.getName());
		output.setTagId(entity.getTagId());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(tagsAppService.findById(entity.getTagId())).thenReturn(output);
        
        
		
		UpdateTagsInput tagsInput = new UpdateTagsInput();
		tagsInput.setName(entity.getName());
		tagsInput.setTagId(entity.getTagId());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(tagsInput);
	
		mvc.perform(put("/tags/" + entity.getTagId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Tags de = createUpdateEntity();
		de.setTagId(entity.getTagId());
		tags_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/tags?search=tagId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	
	@Test
	public void GetPostTags_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("tagId", "1");
		
        Mockito.when(tagsAppService.parsePostTagsJoinColumn("tagId")).thenReturn(joinCol);
		mvc.perform(get("/tags/1/postTags?search=tagId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetPostTags_searchIsNotEmpty() {
	
		Mockito.when(tagsAppService.parsePostTagsJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/tags/1/postTags?search=tagId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
    
}

