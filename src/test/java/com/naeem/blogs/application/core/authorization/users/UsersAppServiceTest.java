package com.naeem.blogs.application.core.authorization.users;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.naeem.blogs.domain.core.authorization.users.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.authorization.users.dto.*;
import com.naeem.blogs.domain.core.authorization.users.QUsers;
import com.naeem.blogs.domain.core.authorization.users.Users;

import com.naeem.blogs.domain.core.authorization.userspreference.Userspreference;
import com.naeem.blogs.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
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
	protected IUserspreferenceRepository _userspreferenceRepository;

	@Mock
	protected IUsersMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
	@Mock
	protected IRoleRepository _roleRepository;

    protected static Long ID=15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findUsersById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Users> nullOptional = Optional.ofNullable(null);
		Mockito.when(_usersRepository.findById(anyLong())).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findUsersById_IdIsNotNullAndIdExists_ReturnUsers() {

		Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
		Userspreference userspreference = new Userspreference();
		Optional<Userspreference> userspreferenceOptional = Optional.of((Userspreference) userspreference);
		Mockito.when(_userspreferenceRepository.findById(ID )).thenReturn(userspreferenceOptional);
		Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
		
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.usersToFindUsersByIdOutput(users,userspreference));
	}
	
	
	@Test 
    public void createUsers_UsersIsNotNullAndUsersDoesNotExist_StoreUsers() { 
 
        Users usersEntity = mock(Users.class); 
    	CreateUsersInput usersInput = new CreateUsersInput();
		
		Userspreference userspreference = new Userspreference();
		Optional<Userspreference> userspreferenceOptional = Optional.of((Userspreference) userspreference);
		Mockito.when(_userspreferenceRepository.findById(ID )).thenReturn(userspreferenceOptional);
		
        Mockito.when(_mapper.createUsersInputToUsers(any(CreateUsersInput.class))).thenReturn(usersEntity); 
        Mockito.when(_usersRepository.save(any(Users.class))).thenReturn(usersEntity);

		Assertions.assertThat(_appService.create(usersInput)).isEqualTo(_mapper.usersToCreateUsersOutput(usersEntity,userspreference)); 

    } 
	@Test
	public void updateUsers_UsersIdIsNotNullAndIdExists_ReturnUpdatedUsers() {

		Users usersEntity = mock(Users.class);
		UpdateUsersInput users= mock(UpdateUsersInput.class);
		
		Optional<Users> usersOptional = Optional.of((Users) usersEntity);
		Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
	 		
		Mockito.when(_mapper.updateUsersInputToUsers(any(UpdateUsersInput.class))).thenReturn(usersEntity);
		Mockito.when(_usersRepository.save(any(Users.class))).thenReturn(usersEntity);
		Assertions.assertThat(_appService.update(ID,users)).isEqualTo(_mapper.usersToUpdateUsersOutput(usersEntity));
	}
    
	@Test
	public void deleteUsers_UsersIsNotNullAndUsersExists_UsersRemoved() {

		Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
		Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
		Userspreference userspreference = mock(Userspreference.class);
		Optional<Userspreference> userspreferenceOptional = Optional.of((Userspreference) userspreference);
		Mockito.when(_usersRepository.findById(anyLong())).thenReturn(usersOptional);
		Mockito.when(_userspreferenceRepository.findById(anyLong())).thenReturn(userspreferenceOptional);
		doNothing().when(_userspreferenceRepository).delete(any(Userspreference.class));
 		
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

	    Userspreference userspreference = new Userspreference();
		Optional<Userspreference> userspreferenceOptional = Optional.of((Userspreference) userspreference);
		Mockito.when(_userspreferenceRepository.findById(any(Long.class))).thenReturn(userspreferenceOptional);
		
		output.add(_mapper.usersToFindUsersByIdOutput(users, userspreference));
		
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
        map.put("emailAddress",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(users.emailAddress.eq("xyz"));
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
        list.add("emailAddress");
        list.add("firstName");
        list.add("lastName");
        list.add("password");
        list.add("phoneNumber");
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
        fields.setFieldName("emailAddress");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(users.emailAddress.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QUsers.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}

	
	@Test 
	public void findUsersByName_NameIsNotNullAndUsersDoesNotExist_ReturnNull() {
		Mockito.when(_usersRepository.findByUsernameIgnoreCase(anyString())).thenReturn(null);	
		Assertions.assertThat(_appService.findByUsername("User1")).isEqualTo(null);	
	}

	@Test
	public void findUsersByName_NameIsNotNullAndUsersExists_ReturnAUsers() {

		Users users = mock(Users.class);
		Mockito.when(_usersRepository.findByUsernameIgnoreCase(anyString())).thenReturn(users);
		Assertions.assertThat(_appService.findByUsername("User1")).isEqualTo(_mapper.usersToFindUsersByUsernameOutput(users));
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
	@Test
	public void ParseuserspermissionsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("usersUserId", keyString);
		Assertions.assertThat(_appService.parseUserspermissionsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParseusersrolesJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("usersUserId", keyString);
		Assertions.assertThat(_appService.parseUsersrolesJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
