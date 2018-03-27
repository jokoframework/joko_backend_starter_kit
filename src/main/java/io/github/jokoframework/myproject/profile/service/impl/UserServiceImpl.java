package io.github.jokoframework.myproject.profile.service.impl;

import io.github.jokoframework.common.errors.BusinessException;
import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.repositories.UserRepository;
import io.github.jokoframework.myproject.profile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Data Service implementation
 *
 * @author bsandoval
 */
@Service
@Transactional(rollbackFor=BusinessException.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public List<UserEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public UserEntity getById(Long userId) {
        return repository.getOne(userId);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return repository.getByUsername(username);
    }
    
    @Override
    public UserEntity getByPersonId(Long personId) {
        return repository.getByPersonId(personId);
    }

	@Override
	public UserEntity getByIdAndFailIfNotExist(Long userId, Boolean failIfNotExist) throws UserException {
		UserEntity user = repository.findOne(userId);
		if(user==null && failIfNotExist){
			throw UserException.notFound(userId);
		}
		return user;
	}

	@Override
	public UserEntity getExistingUser(Long userId) throws UserException {
		return getByIdAndFailIfNotExist(userId, Boolean.TRUE);
	}	

}
