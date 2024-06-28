package com.naeem.blogs.application.core.users;

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

import com.naeem.blogs.domain.core.users.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.users.dto.*;
import com.naeem.blogs.domain.core.users.QUsers;
import com.naeem.blogs.domain.core.users.Users;

import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UsersAppServiceTest {

	@InjectMocks
	@Spy
	protected UsersAppService _appService;
	@Mock
	protected IUsersRepository _usersRepository;
	
	@Mock
	protected IUsersMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    protected static Integer ID=15;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findUsersById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Users> nullOptional = Optional.ofNullable(null);
		Mockito.when(_usersRepository.findById(any(Integer.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findUsersById_IdIsNotNullAndIdExists_ReturnUsers() {

		Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
		Mockito.when(_usersRepository.findById(any(Integer.class))).thenReturn(usersOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.usersToFindUsersByIdOutput(users));
	}
	
	
	@Test 
    public void createUsers_UsersIsNotNullAndUsersDoesNotExist_StoreUsers() { 
 
        Users usersEntity = mock(Users.class); 
    	CreateUsersInput usersInput = new CreateUsersInput();
		
        Mockito.when(_mapper.createUsersInputToUsers(any(CreateUsersInput.class))).thenReturn(usersEntity); 
        Mockito.when(_usersRepository.save(any(Users.class))).thenReturn(usersEntity);

	   	Assertions.assertThat(_appService.create(usersInput)).isEqualTo(_mapper.usersToCreateUsersOutput(usersEntity));

    } 
	@Test
	public void updateUsers_UsersIdIsNotNullAndIdExists_ReturnUpdatedUsers() {

		Users usersEntity = mock(Users.class);
		UpdateUsersInput users= mock(UpdateUsersInput.class);
		
		Optional<Users> usersOptional = Optional.of((Users) usersEntity);
		Mockito.when(_usersRepository.findById(any(Integer.class))).thenReturn(usersOptional);
	 		
		Mockito.when(_mapper.updateUsersInputToUsers(any(UpdateUsersInput.class))).thenReturn(usersEntity);
		Mockito.when(_usersRepository.save(any(Users.class))).thenReturn(usersEntity);
		Assertions.assertThat(_appService.update(ID,users)).isEqualTo(_mapper.usersToUpdateUsersOutput(usersEntity));
	}
    
	@Test
	public void deleteUsers_UsersIsNotNullAndUsersExists_UsersRemoved() {

		Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
		Mockito.when(_usersRepository.findById(any(Integer.class))).thenReturn(usersOptional);
 		
		_appService.delete(ID); 
		verify(_usersRepository).delete(users);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Users> list = new ArrayList<>();
		Page<Users> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUsersByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_usersRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Users> list = new ArrayList<>();
		Users users = mock(Users.class);
		list.add(users);
    	Page<Users> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUsersByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.usersToFindUsersByIdOutput(users));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_usersRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QUsers users = QUsers.usersEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("email",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(users.email.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(users,map,searchMap)).isEqualTo(builder);
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
        list.add("email");
        list.add("passwordHash");
        list.add("username");
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QUsers users = QUsers.usersEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setFieldName("email");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(users.email.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QUsers.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}

	@Test
	public void ParsecommentsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("authorId", keyString);
		Assertions.assertThat(_appService.parseCommentsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParselikesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("userId", keyString);
		Assertions.assertThat(_appService.parseLikesJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParsepostsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("authorId", keyString);
		Assertions.assertThat(_appService.parsePostsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
