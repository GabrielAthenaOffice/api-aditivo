package com.formulario.athena.dto.security;

public record LoginResponseDTO (UserSimpleDTO userDTO, String cookie){}
