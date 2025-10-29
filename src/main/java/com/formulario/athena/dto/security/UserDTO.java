package com.formulario.athena.dto.security;

import com.formulario.athena.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String cargo;
    private UserRole role;
}