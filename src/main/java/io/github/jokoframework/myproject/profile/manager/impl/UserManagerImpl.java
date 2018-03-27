package io.github.jokoframework.myproject.profile.manager.impl;

import io.github.jokoframework.myproject.mapper.OrikaBeanMapper;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.security.util.SecurityUtils;
import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.service.UserService;
import io.github.jokoframework.myproject.web.dto.UserAuthDTO;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.utils.csv.CsvUtils;
import io.github.jokoframework.utils.dto_mapping.DTOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManagerImpl implements UserManager {

	@Autowired
	private UserService userService;

    @Autowired
    private OrikaBeanMapper mapper;

	@Override
    public List<UserDTO> findAll() {
        return DTOUtils.fromEntityToDTO(userService.findAll(), UserDTO.class);
    }

    @Override
    public UserDTO getByUsername(String username) throws UserException {
        UserEntity user = userService.getByUsername(username);

        if (user != null) {
            return mapper.map(user, UserDTO.class);
        }
        else {
            throw UserException.userNotFound(username);
        }
    }

    @Override
    public boolean checkEndUser(String username, String password) {
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            return  false;
        }

        UserEntity entity = userService.getByUsername(username);

        return entity != null && SecurityUtils.matchPassword(password, entity.getPassword());
    }

    @Override
    public UserAuthDTO checkUser(String username, String password) throws UserException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw UserException.invalidUserPassword();
        }

        UserAuthDTO user = mapper.map(userService.getByUsername(username), UserAuthDTO.class);

        if(user == null){
            throw UserException.invalidUserPassword();
        }

        if(!SecurityUtils.matchPassword(password, user.getPassword())){
            throw UserException.invalidUserPassword();
        }

        return user;
    }

    @Override
    public byte[] exportUsersListToCsv(List<UserDTO> list, List<String> columns) {
        return CsvUtils.convertToCsv(list, columns, UserDTO.class);
    }

}
