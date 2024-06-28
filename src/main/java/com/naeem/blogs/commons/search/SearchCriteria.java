package com.naeem.blogs.commons.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SearchCriteria {
	
	int type;
	String value;
	String operator;
	List<SearchFields> fields = new ArrayList<>();
	Map<String,String> joinColumns = new HashMap<String,String>();
	
	public Map<String,String> getJoinColumns() {
		return joinColumns;
	}
	public void setJoinColumns(Map<String,String> joinColumns) {
		this.joinColumns = joinColumns;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public List<SearchFields> getFields() {
		return fields;
	}
	public void setFields(List<SearchFields> fields) {
		this.fields = fields;
	}
}

