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
import com.naeem.blogs.application.core.categories.CategoriesAppService;
import com.naeem.blogs.application.core.categories.dto.*;
import com.naeem.blogs.domain.core.categories.ICategoriesRepository;
import com.naeem.blogs.domain.core.categories.Categories;

import com.naeem.blogs.application.core.postcategories.PostCategoriesAppService;    
import com.naeem.blogs.DatabaseContainerConfig;
import com.naeem.blogs.domain.core.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class CategoriesControllerTest extends DatabaseContainerConfig{
	
	@Autowired
	protected SortHandlerMethodArgumentResolver sortArgumentResolver;

	@Autowired
	@Qualifier("categoriesRepository") 
	protected ICategoriesRepository categories_repository;
	

	@SpyBean
	@Qualifier("categoriesAppService")
	protected CategoriesAppService categoriesAppService;
	
    @SpyBean
    @Qualifier("postCategoriesAppService")
	protected PostCategoriesAppService  postCategoriesAppService;
	
	@SpyBean
	protected LoggingHelper logHelper;

	@SpyBean
	protected Environment env;

	@Mock
	protected Logger loggerMock;

	protected Categories categories;

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
		em.createNativeQuery("truncate table public.categories CASCADE").executeUpdate();
		em.getTransaction().commit();
	}
	

	public Categories createEntity() {
	
		Categories categoriesEntity = new Categories();
		categoriesEntity.setCategoryId(1);
		categoriesEntity.setDescription("1");
		categoriesEntity.setName("1");
		categoriesEntity.setVersiono(0L);
		
		return categoriesEntity;
	}
    public CreateCategoriesInput createCategoriesInput() {
	
	    CreateCategoriesInput categoriesInput = new CreateCategoriesInput();
  		categoriesInput.setDescription("5");
  		categoriesInput.setName("5");
		
		return categoriesInput;
	}

	public Categories createNewEntity() {
		Categories categories = new Categories();
		categories.setCategoryId(3);
		categories.setDescription("3");
		categories.setName("3");
		
		return categories;
	}
	
	public Categories createUpdateEntity() {
		Categories categories = new Categories();
		categories.setCategoryId(4);
		categories.setDescription("4");
		categories.setName("4");
		
		return categories;
	}
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
    
		final CategoriesController categoriesController = new CategoriesController(categoriesAppService, postCategoriesAppService,
		logHelper,env);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());

		this.mvc = MockMvcBuilders.standaloneSetup(categoriesController)
				.setCustomArgumentResolvers(sortArgumentResolver)
				.setControllerAdvice()
				.build();
	}

	@Before
	public void initTest() {

		categories= createEntity();
		List<Categories> list= categories_repository.findAll();
		if(!list.contains(categories)) {
			categories=categories_repository.save(categories);
		}

	}
	
	@Test
	public void FindById_IdIsValid_ReturnStatusOk() throws Exception {
	
		mvc.perform(get("/categories/" + categories.getCategoryId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}  

	@Test
	public void FindById_IdIsNotValid_ReturnStatusNotFound() {

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/categories/999")
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Not found"));

	}
	@Test
	public void CreateCategories_CategoriesDoesNotExist_ReturnStatusOk() throws Exception {
		CreateCategoriesInput categoriesInput = createCategoriesInput();	
		
		
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
	
		String json = ow.writeValueAsString(categoriesInput);

		mvc.perform(post("/categories").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}     
	

	@Test
	public void DeleteCategories_IdIsNotValid_ThrowEntityNotFoundException() {

        doReturn(null).when(categoriesAppService).findById(999);
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->  mvc.perform(delete("/categories/999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())).hasCause(new EntityNotFoundException("There does not exist a categories with a id=999"));

	}  

	@Test
	public void Delete_IdIsValid_ReturnStatusNoContent() throws Exception {
	
	 	Categories entity =  createNewEntity();
	 	entity.setVersiono(0L);
		entity = categories_repository.save(entity);
		

		FindCategoriesByIdOutput output= new FindCategoriesByIdOutput();
		output.setCategoryId(entity.getCategoryId());
		output.setName(entity.getName());
		
         Mockito.doReturn(output).when(categoriesAppService).findById(entity.getCategoryId());
       
    //    Mockito.when(categoriesAppService.findById(entity.getCategoryId())).thenReturn(output);
        
		mvc.perform(delete("/categories/" + entity.getCategoryId()+"/")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}  


	@Test
	public void UpdateCategories_CategoriesDoesNotExist_ReturnStatusNotFound() throws Exception {
   
        doReturn(null).when(categoriesAppService).findById(999);
        
        UpdateCategoriesInput categories = new UpdateCategoriesInput();
		categories.setCategoryId(999);
  		categories.setDescription("999");
  		categories.setName("999");

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(categories);

		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(
		 	put("/categories/999")
		 	.contentType(MediaType.APPLICATION_JSON)
		 	.content(json))
			.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Unable to update. Categories with id=999 not found."));
	}    

	@Test
	public void UpdateCategories_CategoriesExists_ReturnStatusOk() throws Exception {
		Categories entity =  createUpdateEntity();
		entity.setVersiono(0L);
		
		entity = categories_repository.save(entity);
		FindCategoriesByIdOutput output= new FindCategoriesByIdOutput();
		output.setCategoryId(entity.getCategoryId());
		output.setDescription(entity.getDescription());
		output.setName(entity.getName());
		output.setVersiono(entity.getVersiono());
		
        Mockito.when(categoriesAppService.findById(entity.getCategoryId())).thenReturn(output);
        
        
		
		UpdateCategoriesInput categoriesInput = new UpdateCategoriesInput();
		categoriesInput.setCategoryId(entity.getCategoryId());
		categoriesInput.setName(entity.getName());
		

		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(categoriesInput);
	
		mvc.perform(put("/categories/" + entity.getCategoryId()+"/").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());

		Categories de = createUpdateEntity();
		de.setCategoryId(entity.getCategoryId());
		categories_repository.delete(de);
		

	}    
	@Test
	public void FindAll_SearchIsNotNullAndPropertyIsValid_ReturnStatusOk() throws Exception {

		mvc.perform(get("/categories?search=categoryId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
		
	
	
	@Test
	public void GetPostCategories_searchIsNotEmptyAndPropertyIsValid_ReturnList() throws Exception {

		Map<String,String> joinCol = new HashMap<String,String>();
		joinCol.put("categoryId", "1");
		
        Mockito.when(categoriesAppService.parsePostCategoriesJoinColumn("categoryId")).thenReturn(joinCol);
		mvc.perform(get("/categories/1/postCategories?search=categoryId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
	    		  .andExpect(status().isOk());
	}  
	
	@Test
	public void GetPostCategories_searchIsNotEmpty() {
	
		Mockito.when(categoriesAppService.parsePostCategoriesJoinColumn(anyString())).thenReturn(null);
	 		  		    		  
	    org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/categories/1/postCategories?search=categoryId[equals]=1&limit=10&offset=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())).hasCause(new EntityNotFoundException("Invalid join column"));
	}    
    
}

