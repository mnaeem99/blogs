package com.naeem.blogs.commons.search;

public class SearchFields {
	
	String fieldName;
	String searchValue;
	String startingValue;
	String endingValue;
	String operator;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getStartingValue() {
		return startingValue;
	}
	public void setStartingValue(String startingValue) {
		this.startingValue = startingValue;
	}
	public String getEndingValue() {
		return endingValue;
	}
	public void setEndingValue(String endingValue) {
		this.endingValue = endingValue;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
}

