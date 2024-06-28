package com.naeem.blogs.application.core.tags;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.naeem.blogs.application.core.tags.dto.*;
import com.naeem.blogs.domain.core.tags.ITagsRepository;
import com.naeem.blogs.domain.core.tags.QTags;
import com.naeem.blogs.domain.core.tags.Tags;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import com.naeem.blogs.commons.search.*;
import com.naeem.blogs.commons.logging.LoggingHelper;
import com.querydsl.core.BooleanBuilder;
import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import javax.persistence.EntityNotFoundException;

@Service("tagsAppService")
@RequiredArgsConstructor
public class TagsAppService implements ITagsAppService {
    
	@Qualifier("tagsRepository")
	@NonNull protected final ITagsRepository _tagsRepository;

	
	@Qualifier("ITagsMapperImpl")
	@NonNull protected final ITagsMapper mapper;

	@NonNull protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
	public CreateTagsOutput create(CreateTagsInput input) {

		Tags tags = mapper.createTagsInputToTags(input);

		Tags createdTags = _tagsRepository.save(tags);
		return mapper.tagsToCreateTagsOutput(createdTags);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public UpdateTagsOutput update(Integer tagsId, UpdateTagsInput input) {

		Tags existing = _tagsRepository.findById(tagsId).orElseThrow(() -> new EntityNotFoundException("Tags not found"));

		Tags tags = mapper.updateTagsInputToTags(input);
		tags.setPostTagsSet(existing.getPostTagsSet());
		
		Tags updatedTags = _tagsRepository.save(tags);
		return mapper.tagsToUpdateTagsOutput(updatedTags);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer tagsId) {

		Tags existing = _tagsRepository.findById(tagsId).orElseThrow(() -> new EntityNotFoundException("Tags not found"));
	 	
        if(existing !=null) {
			_tagsRepository.delete(existing);
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public FindTagsByIdOutput findById(Integer tagsId) {

		Tags foundTags = _tagsRepository.findById(tagsId).orElse(null);
		if (foundTags == null)  
			return null; 
 	   
 	    return mapper.tagsToFindTagsByIdOutput(foundTags);
	}

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FindTagsByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException  {

		Page<Tags> foundTags = _tagsRepository.findAll(search(search), pageable);
		List<Tags> tagsList = foundTags.getContent();
		Iterator<Tags> tagsIterator = tagsList.iterator(); 
		List<FindTagsByIdOutput> output = new ArrayList<>();

		while (tagsIterator.hasNext()) {
		Tags tags = tagsIterator.next();
 	    output.add(mapper.tagsToFindTagsByIdOutput(tags));
		}
		return output;
	}
	
	protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException {

		QTags tags= QTags.tagsEntity;
		if(search != null) {
			Map<String,SearchFields> map = new HashMap<>();
			for(SearchFields fieldDetails: search.getFields())
			{
				map.put(fieldDetails.getFieldName(),fieldDetails);
			}
			List<String> keysList = new ArrayList<String>(map.keySet());
			checkProperties(keysList);
			return searchKeyValuePair(tags, map,search.getJoinColumns());
		}
		return null;
	}
	
	protected void checkProperties(List<String> list) throws MalformedURLException  {
		for (int i = 0; i < list.size(); i++) {
			if(!(
				list.get(i).replace("%20","").trim().equals("name") ||
				list.get(i).replace("%20","").trim().equals("tagId")
			)) 
			{
			 throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!" );
			}
		}
	}
	
	protected BooleanBuilder searchKeyValuePair(QTags tags, Map<String,SearchFields> map,Map<String,String> joinColumns) {
		BooleanBuilder builder = new BooleanBuilder();
        
		Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();

            if(details.getKey().replace("%20","").trim().equals("name")) {
				if(details.getValue().getOperator().equals("contains")) {
					builder.and(tags.name.likeIgnoreCase("%"+ details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals")) {
					builder.and(tags.name.eq(details.getValue().getSearchValue()));
				} else if(details.getValue().getOperator().equals("notEqual")) {
					builder.and(tags.name.ne(details.getValue().getSearchValue()));
				}
			}
			if(details.getKey().replace("%20","").trim().equals("tagId")) {
				if(details.getValue().getOperator().equals("contains") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(tags.tagId.like(details.getValue().getSearchValue() + "%"));
				} else if(details.getValue().getOperator().equals("equals") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(tags.tagId.eq(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("notEqual") && StringUtils.isNumeric(details.getValue().getSearchValue())) {
					builder.and(tags.tagId.ne(Integer.valueOf(details.getValue().getSearchValue())));
				} else if(details.getValue().getOperator().equals("range")) {
				   if(StringUtils.isNumeric(details.getValue().getStartingValue()) && StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(tags.tagId.between(Integer.valueOf(details.getValue().getStartingValue()), Integer.valueOf(details.getValue().getEndingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getStartingValue())) {
                	   builder.and(tags.tagId.goe(Integer.valueOf(details.getValue().getStartingValue())));
                   } else if(StringUtils.isNumeric(details.getValue().getEndingValue())) {
                	   builder.and(tags.tagId.loe(Integer.valueOf(details.getValue().getEndingValue())));
				   }
				}
			}
	    
		}
		
		return builder;
	}
	
	public Map<String,String> parsePostTagsJoinColumn(String keysString) {
		
		Map<String,String> joinColumnMap = new HashMap<String,String>();
		joinColumnMap.put("tagId", keysString);
		  
		return joinColumnMap;
	}
    
}



