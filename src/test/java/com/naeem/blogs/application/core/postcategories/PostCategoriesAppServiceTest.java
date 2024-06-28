package com.naeem.blogs.application.core.postcategories;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naeem.blogs.domain.core.postcategories.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.postcategories.dto.*;
import com.naeem.blogs.domain.core.postcategories.QPostCategories;
import com.naeem.blogs.domain.core.postcategories.PostCategories;
import com.naeem.blogs.domain.core.postcategories.PostCategoriesId;

import com.naeem.blogs.domain.core.categories.Categories;
import com.naeem.blogs.domain.core.categories.ICategoriesRepository;
import com.naeem.blogs.domain.core.posts.Posts;
import com.naeem.blogs.domain.core.posts.IPostsRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class PostCategoriesAppServiceTest {

	@InjectMocks
	@Spy
	protected PostCategoriesAppService _appService;
	@Mock
	protected IPostCategoriesRepository _postCategoriesRepository;
	
    @Mock
	protected ICategoriesRepository _categoriesRepository;

    @Mock
	protected IPostsRepository _postsRepository;

	@Mock
	protected IPostCategoriesMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    @Mock
    protected PostCategoriesId postCategoriesId;
    
    private static final Long ID = 15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findPostCategoriesById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<PostCategories> nullOptional = Optional.ofNullable(null);
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(postCategoriesId)).isEqualTo(null);
	}
	
	@Test
	public void findPostCategoriesById_IdIsNotNullAndIdExists_ReturnPostCategories() {

		PostCategories postCategories = mock(PostCategories.class);
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategories);
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);
		
	    Assertions.assertThat(_appService.findById(postCategoriesId)).isEqualTo(_mapper.postCategoriesToFindPostCategoriesByIdOutput(postCategories));
	}
	
	
	@Test 
    public void createPostCategories_PostCategoriesIsNotNullAndPostCategoriesDoesNotExist_StorePostCategories() { 
 
        PostCategories postCategoriesEntity = mock(PostCategories.class); 
    	CreatePostCategoriesInput postCategoriesInput = new CreatePostCategoriesInput();
		
        Categories categories = mock(Categories.class);
		Optional<Categories> categoriesOptional = Optional.of((Categories) categories);
        postCategoriesInput.setCategoryId(15);
		
        Mockito.when(_categoriesRepository.findById(any(Integer.class))).thenReturn(categoriesOptional);
        
		
        Posts posts = mock(Posts.class);
		Optional<Posts> postsOptional = Optional.of((Posts) posts);
        postCategoriesInput.setPostId(15);
		
        Mockito.when(_postsRepository.findById(any(Integer.class))).thenReturn(postsOptional);
        
		
        Mockito.when(_mapper.createPostCategoriesInputToPostCategories(any(CreatePostCategoriesInput.class))).thenReturn(postCategoriesEntity); 
        Mockito.when(_postCategoriesRepository.save(any(PostCategories.class))).thenReturn(postCategoriesEntity);

	   	Assertions.assertThat(_appService.create(postCategoriesInput)).isEqualTo(_mapper.postCategoriesToCreatePostCategoriesOutput(postCategoriesEntity));

    } 
    @Test
	public void createPostCategories_PostCategoriesIsNotNullAndPostCategoriesDoesNotExistAndChildIsNullAndChildIsNotMandatory_StorePostCategories() {

		PostCategories postCategories = mock(PostCategories.class);
		CreatePostCategoriesInput postCategoriesInput = mock(CreatePostCategoriesInput.class);
		
		
		Mockito.when(_mapper.createPostCategoriesInputToPostCategories(any(CreatePostCategoriesInput.class))).thenReturn(postCategories);
		Mockito.when(_postCategoriesRepository.save(any(PostCategories.class))).thenReturn(postCategories);
	    Assertions.assertThat(_appService.create(postCategoriesInput)).isEqualTo(_mapper.postCategoriesToCreatePostCategoriesOutput(postCategories)); 
	}
	
    @Test
	public void updatePostCategories_PostCategoriesIsNotNullAndPostCategoriesDoesNotExistAndChildIsNullAndChildIsNotMandatory_ReturnUpdatedPostCategories() {

		PostCategories postCategories = mock(PostCategories.class);
		UpdatePostCategoriesInput postCategoriesInput = mock(UpdatePostCategoriesInput.class);
		
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategories);
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);
		
		Mockito.when(_mapper.updatePostCategoriesInputToPostCategories(any(UpdatePostCategoriesInput.class))).thenReturn(postCategories);
		Mockito.when(_postCategoriesRepository.save(any(PostCategories.class))).thenReturn(postCategories);
		Assertions.assertThat(_appService.update(postCategoriesId,postCategoriesInput)).isEqualTo(_mapper.postCategoriesToUpdatePostCategoriesOutput(postCategories));
	}
	
		
	@Test
	public void updatePostCategories_PostCategoriesIdIsNotNullAndIdExists_ReturnUpdatedPostCategories() {

		PostCategories postCategoriesEntity = mock(PostCategories.class);
		UpdatePostCategoriesInput postCategories= mock(UpdatePostCategoriesInput.class);
		
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategoriesEntity);
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);
	 		
		Mockito.when(_mapper.updatePostCategoriesInputToPostCategories(any(UpdatePostCategoriesInput.class))).thenReturn(postCategoriesEntity);
		Mockito.when(_postCategoriesRepository.save(any(PostCategories.class))).thenReturn(postCategoriesEntity);
		Assertions.assertThat(_appService.update(postCategoriesId,postCategories)).isEqualTo(_mapper.postCategoriesToUpdatePostCategoriesOutput(postCategoriesEntity));
	}
    
	@Test
	public void deletePostCategories_PostCategoriesIsNotNullAndPostCategoriesExists_PostCategoriesRemoved() {

		PostCategories postCategories = mock(PostCategories.class);
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategories);
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);
 		
		_appService.delete(postCategoriesId); 
		verify(_postCategoriesRepository).delete(postCategories);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<PostCategories> list = new ArrayList<>();
		Page<PostCategories> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostCategoriesByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_postCategoriesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<PostCategories> list = new ArrayList<>();
		PostCategories postCategories = mock(PostCategories.class);
		list.add(postCategories);
    	Page<PostCategories> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPostCategoriesByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.postCategoriesToFindPostCategoriesByIdOutput(postCategories));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_postCategoriesRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QPostCategories postCategories = QPostCategories.postCategoriesEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(postCategories,map,searchMap)).isEqualTo(builder);
	}
	
	@Test (expected = Exception.class)
	public void checkProperties_PropertyDoesNotExist_ThrowException() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("xyz");
		_appService.checkProperties(list);
	}
	
	@Test
	public void checkProperties_PropertyExists_ReturnNothing() throws Exception {
		List<String> list = new ArrayList<>();
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QPostCategories postCategories = QPostCategories.postCategoriesEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QPostCategories.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Categories
	@Test
	public void GetCategories_IfPostCategoriesIdAndCategoriesIdIsNotNullAndPostCategoriesExists_ReturnCategories() {
		PostCategories postCategories = mock(PostCategories.class);
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategories);
		Categories categoriesEntity = mock(Categories.class);

		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);

		Mockito.when(postCategories.getCategories()).thenReturn(categoriesEntity);
		Assertions.assertThat(_appService.getCategories(postCategoriesId)).isEqualTo(_mapper.categoriesToGetCategoriesOutput(categoriesEntity, postCategories));
	}

	@Test 
	public void GetCategories_IfPostCategoriesIdAndCategoriesIdIsNotNullAndPostCategoriesDoesNotExist_ReturnNull() {
		Optional<PostCategories> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getCategories(postCategoriesId)).isEqualTo(null);
	}
   
    //Posts
	@Test
	public void GetPosts_IfPostCategoriesIdAndPostsIdIsNotNullAndPostCategoriesExists_ReturnPosts() {
		PostCategories postCategories = mock(PostCategories.class);
		Optional<PostCategories> postCategoriesOptional = Optional.of((PostCategories) postCategories);
		Posts postsEntity = mock(Posts.class);

		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(postCategoriesOptional);

		Mockito.when(postCategories.getPosts()).thenReturn(postsEntity);
		Assertions.assertThat(_appService.getPosts(postCategoriesId)).isEqualTo(_mapper.postsToGetPostsOutput(postsEntity, postCategories));
	}

	@Test 
	public void GetPosts_IfPostCategoriesIdAndPostsIdIsNotNullAndPostCategoriesDoesNotExist_ReturnNull() {
		Optional<PostCategories> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_postCategoriesRepository.findById(any(PostCategoriesId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPosts(postCategoriesId)).isEqualTo(null);
	}

  
	@Test
	public void ParsePostCategoriesKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnPostCategoriesId()
	{
		String keyString= "categoryId=15,postId=15";
	
		PostCategoriesId postCategoriesId = new PostCategoriesId();
        postCategoriesId.setCategoryId(15);
        postCategoriesId.setPostId(15);

		Assertions.assertThat(_appService.parsePostCategoriesKey(keyString)).isEqualToComparingFieldByField(postCategoriesId);
	}
	
	@Test
	public void ParsePostCategoriesKey_KeysStringIsEmpty_ReturnNull()
	{
		String keyString= "";
		Assertions.assertThat(_appService.parsePostCategoriesKey(keyString)).isEqualTo(null);
	}
	
	@Test
	public void ParsePostCategoriesKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
		String keyString= "categoryId";

		Assertions.assertThat(_appService.parsePostCategoriesKey(keyString)).isEqualTo(null);
	}
}
