package com.formulario.athena.mapper;

import com.formulario.athena.dto.security.UserCreateDTO;
import com.formulario.athena.dto.security.UserDTO;
import com.formulario.athena.dto.security.UserSimpleDTO;
import com.formulario.athena.model.UserAthena;

public class UserMapper {

    public static UserDTO toDTO(UserAthena user) {
        return new UserDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getCargo(),
                user.getRole()
        );
    }

    public static UserAthena toEntity(UserCreateDTO dto) {
        UserAthena user = new UserAthena();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        user.setCargo(dto.getCargo());
        user.setRole(dto.getRole());
        return user;
    }

    public static UserSimpleDTO toSimpleDTO(UserAthena user) {
        return new UserSimpleDTO(user.getId(), user.getNome(), user.getEmail());
    }

    public static UserAthena toUser(UserDTO dto) {
        UserAthena user = new UserAthena();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        user.setCargo(dto.getCargo());
        user.setRole(dto.getRole());
        return user;
    }

}

