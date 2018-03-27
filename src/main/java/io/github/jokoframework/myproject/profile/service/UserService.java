package io.github.jokoframework.myproject.profile.service;

import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.exceptions.UserException;

import java.util.List;

/**
 * Methods for user management
 * 
 * @author bsandoval
 *
 */
public interface UserService {
    /**
     * Gets registered end users
     *
     * @return userList - end users
     */
    List<UserEntity> findAll();

    /**
     * Get a user based on its id
     *
     * @param userId user identification
     * @return user
     */
    UserEntity getById(Long userId);
    
    /**
     * Get a user based on its id
     *
     * @param userId user identification
     * @return user
     * @throws UserException 
     */
    UserEntity getByIdAndFailIfNotExist(Long userId, Boolean failIfNotExist) throws UserException;

    /**
     * Get a user based on its user name
     *
     * @param username user name
     * @return user
     */
    UserEntity getByUsername(String username);

    /**
     * Get a user based on the person id
     * @param personId person id
     * @return user
     */
    UserEntity getByPersonId(Long personId);
    
    /**
     * Get existing user or error
     *
     * @param userId user identifier
     * @return user
     * @throws UserException if user doesn't exists
     */
    UserEntity getExistingUser(Long userId) throws UserException;
}
