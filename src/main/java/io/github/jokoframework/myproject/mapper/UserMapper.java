package io.github.jokoframework.myproject.mapper;

import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.myproject.profile.entities.UserEntity;
import io.github.jokoframework.myproject.web.dto.UserAuthDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    ModelMapper modelMapper;

    public UserMapper() {
        modelMapper = new ModelMapper();
    }

    public UserDTO toDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }

    ;

    public UserAuthDTO toAuthDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserAuthDTO.class);
    }
}
