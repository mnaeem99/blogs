package com.naeem.blogs.domain.core.authorization.userspreference;

import com.querydsl.core.types.Predicate;

import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserspreferenceManager implements IUserspreferenceManager {

	@NonNull private final IUserspreferenceRepository  _userspreferenceRepository;

	public Userspreference create(Userspreference userspreference) {

		return _userspreferenceRepository.save(userspreference);
	}
	
	public void delete(Userspreference userspreference) {

		_userspreferenceRepository.delete(userspreference);	
	}

	public Userspreference update(Userspreference userspreference) {

		return _userspreferenceRepository.save(userspreference);
	}
    
    public Userspreference findById(Long usersId) {
		Optional<Userspreference> dbUsers= _userspreferenceRepository.findById(usersId);
		if(dbUsers.isPresent()) {
			Userspreference existingUsers = dbUsers.get();
			return existingUsers;
		} else {
			return null;
		}
	}

	public Page<Userspreference> findAll(Predicate predicate, Pageable pageable) {

		return _userspreferenceRepository.findAll(predicate,pageable);
	}


}


