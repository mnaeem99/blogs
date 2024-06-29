package com.naeem.blogs.application.core.authorization.permission;

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

import com.naeem.blogs.domain.core.authorization.permission.*;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.application.core.authorization.permission.dto.*;
import com.naeem.blogs.domain.core.authorization.permission.QPermission;
import com.naeem.blogs.domain.core.authorization.permission.Permission;

import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class PermissionAppServiceTest {

	@InjectMocks
	@Spy
	protected PermissionAppService _appService;
	@Mock
	protected IPermissionRepository _permissionRepository;
	
	@Mock
	protected IPermissionMapper _mapper;

	@Mock
	protected Logger loggerMock;

	@Mock
	protected LoggingHelper logHelper;
	
    protected static Long ID=15L;
	 
	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(_appService);
		when(logHelper.getLogger()).thenReturn(loggerMock);
		doNothing().when(loggerMock).error(anyString());
	}
	
	@Test
	public void findPermissionById_IdIsNotNullAndIdDoesNotExist_ReturnNull() {
		Optional<Permission> nullOptional = Optional.ofNullable(null);
		Mockito.when(_permissionRepository.findById(anyLong())).thenReturn(nullOptional);
		Assertions.assertThat(_appService.findById(ID )).isEqualTo(null);
	}
	
	@Test
	public void findPermissionById_IdIsNotNullAndIdExists_ReturnPermission() {

		Permission permission = mock(Permission.class);
		Optional<Permission> permissionOptional = Optional.of((Permission) permission);
		Mockito.when(_permissionRepository.findById(anyLong())).thenReturn(permissionOptional);
		
	    Assertions.assertThat(_appService.findById(ID )).isEqualTo(_mapper.permissionToFindPermissionByIdOutput(permission));
	}
	
	@Test 
	public void findPermissionByName_NameIsNotNullAndPermissionDoesNotExist_ReturnNull() {
		
		Mockito.when(_permissionRepository.findByPermissionName(anyString())).thenReturn(null);	
		Assertions.assertThat(_appService.findByPermissionName("Permission1")).isEqualTo(null);	
	}

	@Test
	public void findPermissionByName_NameIsNotNullAndPermissionExists_ReturnAPermission() {

		Permission permission = mock(Permission.class);

		Mockito.when(_permissionRepository.findByPermissionName(anyString())).thenReturn(permission);
		Assertions.assertThat(_appService.findByPermissionName("Permission1")).isEqualTo(_mapper.permissionToFindPermissionByNameOutput(permission));
	}
	
	@Test 
    public void createPermission_PermissionIsNotNullAndPermissionDoesNotExist_StorePermission() { 
 
        Permission permissionEntity = mock(Permission.class); 
    	CreatePermissionInput permissionInput = new CreatePermissionInput();
		
        Mockito.when(_mapper.createPermissionInputToPermission(any(CreatePermissionInput.class))).thenReturn(permissionEntity); 
        Mockito.when(_permissionRepository.save(any(Permission.class))).thenReturn(permissionEntity);

	   	Assertions.assertThat(_appService.create(permissionInput)).isEqualTo(_mapper.permissionToCreatePermissionOutput(permissionEntity));

    } 
	@Test
	public void updatePermission_PermissionIdIsNotNullAndIdExists_ReturnUpdatedPermission() {

		Permission permissionEntity = mock(Permission.class);
		UpdatePermissionInput permission= mock(UpdatePermissionInput.class);
		
		Optional<Permission> permissionOptional = Optional.of((Permission) permissionEntity);
		Mockito.when(_permissionRepository.findById(anyLong())).thenReturn(permissionOptional);
	 		
		Mockito.when(_mapper.updatePermissionInputToPermission(any(UpdatePermissionInput.class))).thenReturn(permissionEntity);
		Mockito.when(_permissionRepository.save(any(Permission.class))).thenReturn(permissionEntity);
		Assertions.assertThat(_appService.update(ID,permission)).isEqualTo(_mapper.permissionToUpdatePermissionOutput(permissionEntity));
	}
    
	@Test
	public void deletePermission_PermissionIsNotNullAndPermissionExists_PermissionRemoved() {

		Permission permission = mock(Permission.class);
		Optional<Permission> permissionOptional = Optional.of((Permission) permission);
		Mockito.when(_permissionRepository.findById(anyLong())).thenReturn(permissionOptional);
 		
		_appService.delete(ID); 
		verify(_permissionRepository).delete(permission);
	}
	
	@Test
	public void find_ListIsEmpty_ReturnList() throws Exception {

		List<Permission> list = new ArrayList<>();
		Page<Permission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPermissionByIdOutput> output = new ArrayList<>();
		SearchCriteria search= new SearchCriteria();

		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
		Mockito.when(_permissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void find_ListIsNotEmpty_ReturnList() throws Exception {

		List<Permission> list = new ArrayList<>();
		Permission permission = mock(Permission.class);
		list.add(permission);
    	Page<Permission> foundPage = new PageImpl(list);
		Pageable pageable = mock(Pageable.class);
		List<FindPermissionByIdOutput> output = new ArrayList<>();
        SearchCriteria search= new SearchCriteria();

		output.add(_mapper.permissionToFindPermissionByIdOutput(permission));
		
		Mockito.when(_appService.search(any(SearchCriteria.class))).thenReturn(new BooleanBuilder());
    	Mockito.when(_permissionRepository.findAll(any(Predicate.class),any(Pageable.class))).thenReturn(foundPage);
		Assertions.assertThat(_appService.find(search, pageable)).isEqualTo(output);
	}
	
	@Test
	public void searchKeyValuePair_PropertyExists_ReturnBooleanBuilder() {
		QPermission permission = QPermission.permissionEntity;
	    SearchFields searchFields = new SearchFields();
		searchFields.setOperator("equals");
		searchFields.setSearchValue("xyz");
	    Map<String,SearchFields> map = new HashMap<>();
        map.put("displayName",searchFields);
		Map<String,String> searchMap = new HashMap<>();
        searchMap.put("xyz",String.valueOf(ID));
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(permission.displayName.eq("xyz"));
		Assertions.assertThat(_appService.searchKeyValuePair(permission,map,searchMap)).isEqualTo(builder);
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
        list.add("displayName");
        list.add("name");
		_appService.checkProperties(list);
	}
	
	@Test
	public void search_SearchIsNotNullAndSearchContainsCaseThree_ReturnBooleanBuilder() throws Exception {
	
		Map<String,SearchFields> map = new HashMap<>();
		QPermission permission = QPermission.permissionEntity;
		List<SearchFields> fieldsList= new ArrayList<>();
		SearchFields fields=new SearchFields();
		SearchCriteria search= new SearchCriteria();
		search.setType(3);
		search.setValue("xyz");
		search.setOperator("equals");
        fields.setFieldName("displayName");
        fields.setOperator("equals");
		fields.setSearchValue("xyz");
        fieldsList.add(fields);
        search.setFields(fieldsList);
		BooleanBuilder builder = new BooleanBuilder();
        builder.or(permission.displayName.eq("xyz"));
        Mockito.doNothing().when(_appService).checkProperties(any(List.class));
		Mockito.doReturn(builder).when(_appService).searchKeyValuePair(any(QPermission.class), any(HashMap.class), any(HashMap.class));
        
		Assertions.assertThat(_appService.search(search)).isEqualTo(builder);
	}
	
	@Test
	public void search_StringIsNull_ReturnNull() throws Exception {

		Assertions.assertThat(_appService.search(null)).isEqualTo(null);
	}

	@Test
	public void ParserolepermissionsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("permissionId", keyString);
		Assertions.assertThat(_appService.parseRolepermissionsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
	@Test
	public void ParseuserspermissionsJoinColumn_KeysStringIsNotEmptyAndKeyValuePairDoesNotExist_ReturnNull()
	{
	    Map<String,String> joinColumnMap = new HashMap<String,String>();
		String keyString= "15";
		joinColumnMap.put("permissionId", keyString);
		Assertions.assertThat(_appService.parseUserspermissionsJoinColumn(keyString)).isEqualTo(joinColumnMap);
	}
}
