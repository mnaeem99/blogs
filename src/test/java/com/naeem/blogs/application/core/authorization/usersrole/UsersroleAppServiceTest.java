package com.naeem.blogs.application.core.authorization.usersrole;

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

import com.naeem.blogs.domain.core.authorization.usersrole.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.authorization.usersrole.dto.*;
import com.naeem.blogs.domain.core.authorization.usersrole.QUsersrole;
import com.naeem.blogs.domain.core.authorization.usersrole.Usersrole;
import com.naeem.blogs.domain.core.authorization.usersrole.UsersroleId;

import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UsersroleAppServiceTest {

	@InjectMocks
	@Spy
	protected UsersroleAppService _appService;
	@Mock
	protected IUsersroleRepository _usersroleRepository;
	
    @Mock
	protected IRoleRepository _roleRepository;

    @Mock
	protected IUsersRepository _usersRepository;

	@Mock
	protected IUsersroleMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    @Mock
    protected UsersroleId usersroleId;
    
    private static final Long ID = 15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findUsersroleById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Usersrole> nullOptional = Optional.ofNullable(null);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(usersroleId)).isEqualTo(null);
	}
	
	@Test
	public void findUsersroleById_IdIsNotNullAndIdExists_ReturnUsersrole() {

		Usersrole usersrole = mock(Usersrole.class);
		Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
		
	    Assertions.assertThat(_appService.findById(usersroleId)).isEqualTo(_mapper.usersroleToFindUsersroleByIdOutput(usersrole));
	}
	
	
	@Test 
    public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExist_StoreUsersrole() { 
 
        Usersrole usersroleEntity = mock(Usersrole.class); 
    	CreateUsersroleInput usersroleInput = new CreateUsersroleInput();
		
        Role role = mock(Role.class);
		Optional<Role> roleOptional = Optional.of((Role) role);
        usersroleInput.setRoleId(15L);
		
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(roleOptional);
        
		
        Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
        usersroleInput.setUsersUserId(15L);
		
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);
        
		
        Mockito.when(_mapper.createUsersroleInputToUsersrole(any(CreateUsersroleInput.class))).thenReturn(usersroleEntity); 
        Mockito.when(_usersroleRepository.save(any(Usersrole.class))).thenReturn(usersroleEntity);

	   	Assertions.assertThat(_appService.create(usersroleInput)).isEqualTo(_mapper.usersroleToCreateUsersroleOutput(usersroleEntity));

    } 
	@Test
	public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreateUsersroleInput usersrole = mock(CreateUsersroleInput.class);
		
		Mockito.when(_mapper.createUsersroleInputToUsersrole(any(CreateUsersroleInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(usersrole)).isEqualTo(null);
	}
	
	@Test
	public void createUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreateUsersroleInput usersrole = new CreateUsersroleInput();
	    
        usersrole.setRoleId(15L);
     
     	Optional<Role> nullOptional = Optional.ofNullable(null);
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
//		Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(usersrole)).isEqualTo(null);
    }
    
    @Test
	public void updateUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdateUsersroleInput usersroleInput = mock(UpdateUsersroleInput.class);
		Usersrole usersrole = mock(Usersrole.class); 
		
     	Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
		
		Mockito.when(_mapper.updateUsersroleInputToUsersrole(any(UpdateUsersroleInput.class))).thenReturn(usersrole); 
		Assertions.assertThat(_appService.update(usersroleId,usersroleInput)).isEqualTo(null);
	}
	
	@Test
	public void updateUsersrole_UsersroleIsNotNullAndUsersroleDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdateUsersroleInput usersroleInput = new UpdateUsersroleInput();
        usersroleInput.setRoleId(15L);
     
    	Usersrole usersrole = mock(Usersrole.class);
		
     	Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
		
		Mockito.when(_mapper.updateUsersroleInputToUsersrole(any(UpdateUsersroleInput.class))).thenReturn(usersrole);
     	Optional<Role> nullOptional = Optional.ofNullable(null);
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(usersroleId,usersroleInput)).isEqualTo(null);
	}

		
	@Test
	public void updateUsersrole_UsersroleIdIsNotNullAndIdExists_ReturnUpdatedUsersrole() {

		Usersrole usersroleEntity = mock(Usersrole.class);
		UpdateUsersroleInput usersrole= mock(UpdateUsersroleInput.class);
		
		Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersroleEntity);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
	 		
		Mockito.when(_mapper.updateUsersroleInputToUsersrole(any(UpdateUsersroleInput.class))).thenReturn(usersroleEntity);
		Mockito.when(_usersroleRepository.save(any(Usersrole.class))).thenReturn(usersroleEntity);
		Assertions.assertThat(_appService.update(usersroleId,usersrole)).isEqualTo(_mapper.usersroleToUpdateUsersroleOutput(usersroleEntity));
	}
    
	@Test
	public void deleteUsersrole_UsersroleIsNotNullAndUsersroleExists_UsersroleRemoved() {

		Usersrole usersrole = mock(Usersrole.class);
		Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);
 		
		_appService.delete(usersroleId); 
		verify(_usersroleRepository).delete(usersrole);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Usersrole> list = new ArrayList<>();
		Page<Usersrole> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUsersroleByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_usersroleRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Usersrole> list = new ArrayList<>();
		Usersrole usersrole = mock(Usersrole.class);
		list.add(usersrole);
    	Page<Usersrole> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUsersroleByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.usersroleToFindUsersroleByIdOutput(usersrole));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_usersroleRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QUsersrole usersrole = QUsersrole.usersroleEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(usersrole,map,searchMap)).isEqualTo(builder);
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
		QUsersrole usersrole = QUsersrole.usersroleEntity;
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
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QUsersrole.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Role
	@Test
	public void GetRole_IfUsersroleIdAndRoleIdIsNotNullAndUsersroleExists_ReturnRole() {
		Usersrole usersrole = mock(Usersrole.class);
		Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Role roleEntity = mock(Role.class);

		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

		Mockito.when(usersrole.getRole()).thenReturn(roleEntity);
		Assertions.assertThat(_appService.getRole(usersroleId)).isEqualTo(_mapper.roleToGetRoleOutput(roleEntity, usersrole));
	}

	@Test 
	public void GetRole_IfUsersroleIdAndRoleIdIsNotNullAndUsersroleDoesNotExist_ReturnNull() {
		Optional<Usersrole> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getRole(usersroleId)).isEqualTo(null);
	}
   
    //Users
	@Test
	public void GetUsers_IfUsersroleIdAndUsersIdIsNotNullAndUsersroleExists_ReturnUsers() {
		Usersrole usersrole = mock(Usersrole.class);
		Optional<Usersrole> usersroleOptional = Optional.of((Usersrole) usersrole);
		Users usersEntity = mock(Users.class);

		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(usersroleOptional);

		Mockito.when(usersrole.getUsers()).thenReturn(usersEntity);
		Assertions.assertThat(_appService.getUsers(usersroleId)).isEqualTo(_mapper.usersToGetUsersOutput(usersEntity, usersrole));
	}

	@Test 
	public void GetUsers_IfUsersroleIdAndUsersIdIsNotNullAndUsersroleDoesNotExist_ReturnNull() {
		Optional<Usersrole> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_usersroleRepository.findById(any(UsersroleId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getUsers(usersroleId)).isEqualTo(null);
	}

  
	@Test
	public void ParseUsersroleKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnUsersroleId()
	{
		String keyString= "roleId=15,usersUserId=15";
	
		UsersroleId usersroleId = new UsersroleId();
        usersroleId.setRoleId(15L);
        usersroleId.setUsersUserId(15L);

		Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualToComparingFieldByField(usersroleId);
	}
	
	@Test
	public void ParseUsersroleKey_KeysStringIsEmpty_ReturnNull()
	{
		String keyString= "";
		Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualTo(null);
	}
	
	@Test
	public void ParseUsersroleKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
		String keyString= "roleId";

		Assertions.assertThat(_appService.parseUsersroleKey(keyString)).isEqualTo(null);
	}
}
