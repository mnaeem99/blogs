package com.naeem.blogs.application.core.authorization.rolepermission;

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

import com.naeem.blogs.domain.core.authorization.rolepermission.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.authorization.rolepermission.dto.*;
import com.naeem.blogs.domain.core.authorization.rolepermission.QRolepermission;
import com.naeem.blogs.domain.core.authorization.rolepermission.Rolepermission;
import com.naeem.blogs.domain.core.authorization.rolepermission.RolepermissionId;

import com.naeem.blogs.domain.core.authorization.permission.Permission;
import com.naeem.blogs.domain.core.authorization.permission.IPermissionRepository;
import com.naeem.blogs.domain.core.authorization.role.Role;
import com.naeem.blogs.domain.core.authorization.role.IRoleRepository;
import com.naeem.blogs.domain.core.authorization.usersrole.IUsersroleRepository;
import com.naeem.blogs.security.JWTAppService;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class RolepermissionAppServiceTest {

	@InjectMocks
	@Spy
	protected RolepermissionAppService _appService;
	@Mock
	protected JWTAppService jwtAppService;

	@Mock
	protected IUsersroleRepository _usersroleRepository;

	@Mock
	protected IRolepermissionRepository _rolepermissionRepository;
	
    @Mock
	protected IPermissionRepository _permissionRepository;

    @Mock
	protected IRoleRepository _roleRepository;

	@Mock
	protected IRolepermissionMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    @Mock
    protected RolepermissionId rolepermissionId;
    
    private static final Long ID = 15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findRolepermissionById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Rolepermission> nullOptional = Optional.ofNullable(null);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(rolepermissionId)).isEqualTo(null);
	}
	
	@Test
	public void findRolepermissionById_IdIsNotNullAndIdExists_ReturnRolepermission() {

		Rolepermission rolepermission = mock(Rolepermission.class);
		Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);
		
	    Assertions.assertThat(_appService.findById(rolepermissionId)).isEqualTo(_mapper.rolepermissionToFindRolepermissionByIdOutput(rolepermission));
	}
	
	
	@Test 
    public void createRolepermission_RolepermissionIsNotNullAndRolepermissionDoesNotExist_StoreRolepermission() { 
 
        Rolepermission rolepermissionEntity = mock(Rolepermission.class); 
    	CreateRolepermissionInput rolepermissionInput = new CreateRolepermissionInput();
		
        Permission permission = mock(Permission.class);
		Optional<Permission> permissionOptional = Optional.of((Permission) permission);
        rolepermissionInput.setPermissionId(15L);
		
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(permissionOptional);
        
		
        Role role = mock(Role.class);
		Optional<Role> roleOptional = Optional.of((Role) role);
        rolepermissionInput.setRoleId(15L);
		
        Mockito.when(_roleRepository.findById(any(Long.class))).thenReturn(roleOptional);
        
		
        Mockito.when(_mapper.createRolepermissionInputToRolepermission(any(CreateRolepermissionInput.class))).thenReturn(rolepermissionEntity); 
        Mockito.when(_rolepermissionRepository.save(any(Rolepermission.class))).thenReturn(rolepermissionEntity);

	   	Assertions.assertThat(_appService.create(rolepermissionInput)).isEqualTo(_mapper.rolepermissionToCreateRolepermissionOutput(rolepermissionEntity));

    } 
	@Test
	public void createRolepermission_RolepermissionIsNotNullAndRolepermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		CreateRolepermissionInput rolepermission = mock(CreateRolepermissionInput.class);
		
		Mockito.when(_mapper.createRolepermissionInputToRolepermission(any(CreateRolepermissionInput.class))).thenReturn(null); 
		Assertions.assertThat(_appService.create(rolepermission)).isEqualTo(null);
	}
	
	@Test
	public void createRolepermission_RolepermissionIsNotNullAndRolepermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {

		CreateRolepermissionInput rolepermission = new CreateRolepermissionInput();
	    
        rolepermission.setPermissionId(15L);
     
     	Optional<Permission> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
//		Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.create(rolepermission)).isEqualTo(null);
    }
    
    @Test
	public void updateRolepermission_RolepermissionIsNotNullAndRolepermissionDoesNotExistAndChildIsNullAndChildIsMandatory_ReturnNull() {

		UpdateRolepermissionInput rolepermissionInput = mock(UpdateRolepermissionInput.class);
		Rolepermission rolepermission = mock(Rolepermission.class); 
		
     	Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);
		
		Mockito.when(_mapper.updateRolepermissionInputToRolepermission(any(UpdateRolepermissionInput.class))).thenReturn(rolepermission); 
		Assertions.assertThat(_appService.update(rolepermissionId,rolepermissionInput)).isEqualTo(null);
	}
	
	@Test
	public void updateRolepermission_RolepermissionIsNotNullAndRolepermissionDoesNotExistAndChildIsNotNullAndChildIsMandatoryAndFindByIdIsNull_ReturnNull() {
		
		UpdateRolepermissionInput rolepermissionInput = new UpdateRolepermissionInput();
        rolepermissionInput.setPermissionId(15L);
     
    	Rolepermission rolepermission = mock(Rolepermission.class);
		
     	Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);
		
		Mockito.when(_mapper.updateRolepermissionInputToRolepermission(any(UpdateRolepermissionInput.class))).thenReturn(rolepermission);
     	Optional<Permission> nullOptional = Optional.ofNullable(null);
        Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
        
	//	Mockito.when(_permissionRepository.findById(any(Long.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.update(rolepermissionId,rolepermissionInput)).isEqualTo(null);
	}

		
	@Test
	public void updateRolepermission_RolepermissionIdIsNotNullAndIdExists_ReturnUpdatedRolepermission() {

		Rolepermission rolepermissionEntity = mock(Rolepermission.class);
		UpdateRolepermissionInput rolepermission= mock(UpdateRolepermissionInput.class);
		
		Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermissionEntity);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);
	 		
		Mockito.when(_mapper.updateRolepermissionInputToRolepermission(any(UpdateRolepermissionInput.class))).thenReturn(rolepermissionEntity);
		Mockito.when(_rolepermissionRepository.save(any(Rolepermission.class))).thenReturn(rolepermissionEntity);
		Assertions.assertThat(_appService.update(rolepermissionId,rolepermission)).isEqualTo(_mapper.rolepermissionToUpdateRolepermissionOutput(rolepermissionEntity));
	}
    
	@Test
	public void deleteRolepermission_RolepermissionIsNotNullAndRolepermissionExists_RolepermissionRemoved() {

		Rolepermission rolepermission = mock(Rolepermission.class);
		Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);
 		
		_appService.delete(rolepermissionId); 
		verify(_rolepermissionRepository).delete(rolepermission);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Rolepermission> list = new ArrayList<>();
		Page<Rolepermission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindRolepermissionByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_rolepermissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Rolepermission> list = new ArrayList<>();
		Rolepermission rolepermission = mock(Rolepermission.class);
		list.add(rolepermission);
    	Page<Rolepermission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindRolepermissionByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.rolepermissionToFindRolepermissionByIdOutput(rolepermission));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_rolepermissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QRolepermission rolepermission = QRolepermission.rolepermissionEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
		Assertions.assertThat(_appService.searchKeyValuePair(rolepermission,map,searchMap)).isEqualTo(builder);
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
		QRolepermission rolepermission = QRolepermission.rolepermissionEntity;
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
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QRolepermission.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}
   
    //Permission
	@Test
	public void GetPermission_IfRolepermissionIdAndPermissionIdIsNotNullAndRolepermissionExists_ReturnPermission() {
		Rolepermission rolepermission = mock(Rolepermission.class);
		Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Permission permissionEntity = mock(Permission.class);

		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);

		Mockito.when(rolepermission.getPermission()).thenReturn(permissionEntity);
		Assertions.assertThat(_appService.getPermission(rolepermissionId)).isEqualTo(_mapper.permissionToGetPermissionOutput(permissionEntity, rolepermission));
	}

	@Test 
	public void GetPermission_IfRolepermissionIdAndPermissionIdIsNotNullAndRolepermissionDoesNotExist_ReturnNull() {
		Optional<Rolepermission> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getPermission(rolepermissionId)).isEqualTo(null);
	}
   
    //Role
	@Test
	public void GetRole_IfRolepermissionIdAndRoleIdIsNotNullAndRolepermissionExists_ReturnRole() {
		Rolepermission rolepermission = mock(Rolepermission.class);
		Optional<Rolepermission> rolepermissionOptional = Optional.of((Rolepermission) rolepermission);
		Role roleEntity = mock(Role.class);

		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(rolepermissionOptional);

		Mockito.when(rolepermission.getRole()).thenReturn(roleEntity);
		Assertions.assertThat(_appService.getRole(rolepermissionId)).isEqualTo(_mapper.roleToGetRoleOutput(roleEntity, rolepermission));
	}

	@Test 
	public void GetRole_IfRolepermissionIdAndRoleIdIsNotNullAndRolepermissionDoesNotExist_ReturnNull() {
		Optional<Rolepermission> nullOptional = Optional.ofNullable(null);;
		Mockito.when(_rolepermissionRepository.findById(any(RolepermissionId.class))).thenReturn(nullOptional);
		Assertions.assertThat(_appService.getRole(rolepermissionId)).isEqualTo(null);
	}

  
	@Test
	public void ParseRolepermissionKey_KeysStringIsNotEmptyAndKeyValuePairExists_ReturnRolepermissionId()
	{
		String keyString= "permissionId=15,roleId=15";
	
		RolepermissionId rolepermissionId = new RolepermissionId();
        rolepermissionId.setPermissionId(15L);
        rolepermissionId.setRoleId(15L);

		Assertions.assertThat(_appService.parseRolepermissionKey(keyString)).isEqualToComparingFieldByField(rolepermissionId);
	}
	
	@Test
	public void ParseRolepermissionKey_KeysStringIsEmpty_ReturnNull()
	{
		String keyString= "";
		Assertions.assertThat(_appService.parseRolepermissionKey(keyString)).isEqualTo(null);
	}
	
	@Test
	public void ParseRolepermissionKey_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
		String keyString= "permissionId";

		Assertions.assertThat(_appService.parseRolepermissionKey(keyString)).isEqualTo(null);
	}
}
