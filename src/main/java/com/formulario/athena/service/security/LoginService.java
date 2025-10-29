package com.formulario.athena.service.security;

import com.formulario.athena.config.security.TokenService;
import com.formulario.athena.dto.security.*;
import com.formulario.athena.mapper.UserMapper;
import com.formulario.athena.model.UserAthena;
import com.formulario.athena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public LoginResponseDTO login(AuthenticationDTO data) {
        Authentication authentication = null;

        System.out.println("Senha banco: " + userRepository.findByEmail(data.email()).get().getSenha());
        System.out.println("Senha digitada: " + data.senha());
        System.out.println("Senha confere? " + new BCryptPasswordEncoder().matches(data.senha(), userRepository.findByEmail(data.email()).get().getSenha()));

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.email(), data.senha())
            );
        } catch (AuthenticationException exception) {
            throw new RuntimeException("Bad credentials", exception);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserAthena userDetails = (UserAthena) authentication.getPrincipal();
        ResponseCookie jwtCookie = tokenService.generateCookie(userDetails);

        UserSimpleDTO userSimpleDTO = new UserSimpleDTO(userDetails.getId(), userDetails.getNome(), userDetails.getEmail());

        System.out.println("Authorities: " + userDetails.getAuthorities());
        System.out.println("Role: " + userDetails.getRole());

        return new LoginResponseDTO(userSimpleDTO, jwtCookie.toString());
    }

    public UserAthena registrar(UserCreateDTO data) {
        if (this.userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        UserAthena registro = UserMapper.toEntity(data);

        String encryptedPassword = new BCryptPasswordEncoder().encode(registro.getSenha());
        UserAthena user = new UserAthena(data.getNome(), data.getEmail(), encryptedPassword,
                data.getCargo(), data.getRole());

        return this.userRepository.save(user);
    }

    public UserDTO atualizarUsuario(String id, UserDTO userDTO) {
        UserAthena savedUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UserAthena user = UserMapper.toUser(userDTO);
        user.setId(id);
        if (userDTO.getSenha() != null && !userDTO.getSenha().isBlank()) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.getSenha());
            user.setSenha(encryptedPassword);
        }
        savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);

    }

    public UserDTO deletarUsuario(String id) {
        UserAthena user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        userRepository.delete(user);

        return UserMapper.toDTO(user);
    }




}

