package com.naeem.blogs.application.core.authorization.userspermission;

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

import com.naeem.blogs.domain.core.authorization.userspermission.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.authorization.userspermission.dto.*;
import com.naeem.blogs.domain.core.authorization.userspermission.QUserspermission;
import com.naeem.blogs.domain.core.authorization.userspermission.Userspermission;
import com.naeem.blogs.domain.core.authorization.userspermission.UserspermissionId;

import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.users.Users;
import com.naeem.blogs.domain.core.authorization.users.IUsersRepository;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserspermissionAppServiceTest {

	@InjectMocks
	@Spy
	protected UserspermissionAppService _appService;
	@Mock
	protected IUserspermissionRepository _userspermissionRepository;
	
    @Mock
	protected IPermissionRepository _permissionRepository;

    @Mock
	protected IUsersRepository _usersRepository;

	@Mock
	protected IUserspermissionMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    @Mock
    protected UserspermissionId userspermissionId;
    
    private static final Long ID = 15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findUserspermissionById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Userspermission> nullOptional = Optional.ofNullable(null);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(userspermissionId)).isEqualTo(null);
	}
	
	@Test
	public void findUserspermissionById_IdIsNotNullAndIdExists_ReturnUserspermission() {

		Userspermission userspermission = mock(Userspermission.class);
		Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);
		
	    Assertions.assertThat(_appService.findById(userspermissionId)).isEqualTo(_mapper.userspermissionToFindUserspermissionByIdOutput(userspermission));
	}
	
	
	@Test 
    public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExist_StoreUserspermission() { 
 
        Userspermission userspermissionEntity = mock(Userspermission.class); 
    	CreateUserspermissionInput userspermissionInput = new CreateUserspermissionInput();
		
        Permission permission = mock(Permission.class);
		Optional<Permission> permissionOptional = Optional.of((Permission) permission);
        userspermissionInput.setPermissionId(15L);
		
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(permissionOptional);
        
		
        Users users = mock(Users.class);
		Optional<Users> usersOptional = Optional.of((Users) users);
        userspermissionInput.setUsersUserId(15L);
		
        Mockito.when(_usersRepository.findById(any(Long.class))).thenReturn(usersOptional);
        
		
        Mockito.when(_mapper.createUserspermissionInputToUserspermission(any(CreateUserspermissionInput.class))).thenReturn(userspermissionEntity); 
        Mockito.when(_userspermissionRepository.save(any(Userspermission.class))).thenReturn(userspermissionEntity);

	    CreateUserspermissionOutput output = new CreateUserspermissionOutput();
        Mockito.when(_mapper.userspermissionToCreateUserspermissionOutput(any(Userspermission.class))).thenReturn(output);

	   	Assertions.assertThat(_appService.create(userspermissionInput)).isEqualTo(_mapper.userspermissionToCreateUserspermissionOutput(userspermissionEntity));

    } 
	@Test
	public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreateUserspermissionInput userspermission = mock(CreateUserspermissionInput.class);
		
		Mockito.when(_mapper.createUserspermissionInputToUserspermission(any(CreateUserspermissionInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(userspermission)).isEqualTo(null);
	}
	
	@Test
	public void createUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreateUserspermissionInput userspermission = new CreateUserspermissionInput();
	    
        userspermission.setPermissionId(15L);
     
     	Optional<Permission> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
//		Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(userspermission)).isEqualTo(null);
    }
    
    @Test
	public void updateUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdateUserspermissionInput userspermissionInput = mock(UpdateUserspermissionInput.class);
		Userspermission userspermission = mock(Userspermission.class); 
		
     	Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);
		
		Mockito.when(_mapper.updateUserspermissionInputToUserspermission(any(UpdateUserspermissionInput.class))).thenReturn(userspermission); 
		Assertions.assertThat(_appService.update(userspermissionId,userspermissionInput)).isEqualTo(null);
	}
	
	@Test
	public void updateUserspermission_UserspermissionIsNotNullAndUserspermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdateUserspermissionInput userspermissionInput = new UpdateUserspermissionInput();
        userspermissionInput.setPermissionId(15L);
     
    	Userspermission userspermission = mock(Userspermission.class);
		
     	Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);
		
		Mockito.when(_mapper.updateUserspermissionInputToUserspermission(any(UpdateUserspermissionInput.class))).thenReturn(userspermission);
     	Optional<Permission> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(userspermissionId,userspermissionInput)).isEqualTo(null);
	}

		
	@Test
	public void updateUserspermission_UserspermissionIdIsNotNullAndIdExists_ReturnUpdatedUserspermission() {

		Userspermission userspermissionEntity = mock(Userspermission.class);
		UpdateUserspermissionInput userspermission= mock(UpdateUserspermissionInput.class);
		
		Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermissionEntity);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);
	 		
		Mockito.when(_mapper.updateUserspermissionInputToUserspermission(any(UpdateUserspermissionInput.class))).thenReturn(userspermissionEntity);
		Mockito.when(_userspermissionRepository.save(any(Userspermission.class))).thenReturn(userspermissionEntity);
		Assertions.assertThat(_appService.update(userspermissionId,userspermission)).isEqualTo(_mapper.userspermissionToUpdateUserspermissionOutput(userspermissionEntity));
	}
    
	@Test
	public void deleteUserspermission_UserspermissionIsNotNullAndUserspermissionExists_UserspermissionRemoved() {

		Userspermission userspermission = mock(Userspermission.class);
		Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);
 		
		_appService.delete(userspermissionId); 
		verify(_userspermissionRepository).delete(userspermission);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Userspermission> list = new ArrayList<>();
		Page<Userspermission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUserspermissionByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_userspermissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Userspermission> list = new ArrayList<>();
		Userspermission userspermission = mock(Userspermission.class);
		list.add(userspermission);
    	Page<Userspermission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindUserspermissionByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.userspermissionToFindUserspermissionByIdOutput(userspermission));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_userspermissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QUserspermission userspermission = QUserspermission.userspermissionEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(userspermission,map,searchMap)).isEqualTo(builder);
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
		QUserspermission userspermission = QUserspermission.userspermissionEntity;
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
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QUserspermission.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Permission
	@Test
	public void GetPermission_IfUserspermissionIdAndPermissionIdIsNotNullAndUserspermissionExists_ReturnPermission() {
		Userspermission userspermission = mock(Userspermission.class);
		Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Permission permissionEntity = mock(Permission.class);

		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);

		Mockito.when(userspermission.getPermission()).thenReturn(permissionEntity);
		Assertions.assertThat(_appService.getPermission(userspermissionId)).isEqualTo(_mapper.permissionToGetPermissionOutput(permissionEntity, userspermission));
	}

	@Test 
	public void GetPermission_IfUserspermissionIdAndPermissionIdIsNotNullAndUserspermissionDoesNotExist_ReturnNull() {
		Optional<Userspermission> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPermission(userspermissionId)).isEqualTo(null);
	}
   
    //Users
	@Test
	public void GetUsers_IfUserspermissionIdAndUsersIdIsNotNullAndUserspermissionExists_ReturnUsers() {
		Userspermission userspermission = mock(Userspermission.class);
		Optional<Userspermission> userspermissionOptional = Optional.of((Userspermission) userspermission);
		Users usersEntity = mock(Users.class);

		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(userspermissionOptional);

		Mockito.when(userspermission.getUsers()).thenReturn(usersEntity);
		Assertions.assertThat(_appService.getUsers(userspermissionId)).isEqualTo(_mapper.usersToGetUsersOutput(usersEntity, userspermission));
	}

	@Test 
	public void GetUsers_IfUserspermissionIdAndUsersIdIsNotNullAndUserspermissionDoesNotExist_ReturnNull() {
		Optional<Userspermission> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_userspermissionRepository.findById(any(UserspermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getUsers(userspermissionId)).isEqualTo(null);
	}

  
	@Test
	public void ParseUserspermissionKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnUserspermissionId()
	{
		String keyString= "permissionId=15,usersUserId=15";
	
		UserspermissionId userspermissionId = new UserspermissionId();
        userspermissionId.setPermissionId(15L);
        userspermissionId.setUsersUserId(15L);

		Assertions.assertThat(_appService.parseUserspermissionKey(keyString)).isEqualToComparingFieldByField(userspermissionId);
	}
	
	@Test
	public void ParseUserspermissionKey_KeysStringIsEmpty_ReturnNull()
	{
		String keyString= "";
		Assertions.assertThat(_appService.parseUserspermissionKey(keyString)).isEqualTo(null);
	}
	
	@Test
	public void ParseUserspermissionKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
		String keyString= "permissionId";

		Assertions.assertThat(_appService.parseUserspermissionKey(keyString)).isEqualTo(null);
	}
}
