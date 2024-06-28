package com.naeem.blogs.commons.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Time;

public class SearchUtils {
	public static SearchCriteria generateSearchCriteriaObject(String searchString){
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setType(3);

		List<SearchFields> searchFields = new ArrayList<SearchFields>();

		if(searchString != null && searchString.length() > 0) {
			String[] fields = searchString.split(";");

			for(String field: fields) {
				SearchFields searchField = new SearchFields();

				String fieldName = field.substring(0, field.indexOf('['));
				String operator = field.substring(field.indexOf('[') + 1, field.indexOf(']'));
				String searchValue = field.substring(field.indexOf('=') + 1);

				searchField.setFieldName(fieldName);
				searchField.setOperator(operator);

				if(!operator.equals("range")){
					searchField.setSearchValue(searchValue);
				}
				else {
					String[] rangeValues = searchValue.split(",");
					if(!rangeValues[0].isEmpty()) {
						String startingValue = rangeValues[0];
						searchField.setStartingValue(startingValue);
					}

					if(rangeValues.length == 2) {
						String endingValue = rangeValues[1];
						searchField.setEndingValue(endingValue);
					}
				}

				searchFields.add(searchField);

			}
		}

		searchCriteria.setFields(searchFields);
		return searchCriteria;
	}
	
public static Date stringToDate(String str)
	{
		SimpleDateFormat formatter;
		if(str!=null) {
			str = str.replace("%20","T");
			if(str.contains(" "))
			{
				str = str.replace(" ","T");
			}
			String[] d = str.split("T");
			if(d.length == 2) {
				if(d[1].contains("."))
				{
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				}
				else
				{
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				str = d[0] + " " + d[1];
			} else if (d.length == 1){
				formatter = new SimpleDateFormat("yyyy-MM-dd");
			} else {
				return null;
			}
			Date date;
			try {
				date = formatter.parse(str);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static Time stringToTime(String str)
	{
		if(str!=null)
		{
			try {
				SimpleDateFormat sdf ;
				if(str.contains(".")) {
					sdf = new SimpleDateFormat("HH:mm:ss.SSS");
				}
				else {
					sdf = new SimpleDateFormat("HH:mm:ss");
				}
				long ms = sdf.parse(str).getTime();
				Time time = new Time(ms);

				return time;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public static OffsetDateTime stringToOffsetDateTime(String str)
	{
		try {
			if(str!=null) {
				str = str.replace("%20","T").replace(" ","T");
				DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
				OffsetDateTime date = OffsetDateTime.parse(str,formatter);
				return date;
			}
			else 
				return null;
		}
		catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static LocalDate stringToLocalDate(String str)
	{
		try {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(str,formatter);
		return date;
		}
		catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static OffsetTime stringToOffsetTime(String str)
	{
		try {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;
		OffsetTime time = OffsetTime.parse(str,formatter);
		return time;
		}
		catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static LocalTime stringToLocalTime(String str)
	{
		try {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalTime time = LocalTime.parse(str,formatter);
		return time;
		}
		catch (DateTimeParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static LocalDateTime stringToLocalDateTime(String str)
	{
		DateTimeFormatter formatter; 
	
		if(str.contains("."))
		{
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		}
		else
		{
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		}
	
		LocalDateTime date = LocalDateTime.parse(str,formatter);
		return date;
	
	}
}

