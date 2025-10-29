package com.formulario.athena.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAthena implements UserDetails {

    @Id
    private String id; // ObjectId em string

    private String nome;

    @Indexed(unique = true)
    private String email;

    private String senha;   // <<< guarda HASH, não a senha

    private String cargo;

    private UserRole role;      // ADMIN, FUNCIONARIO, ESTAGIARIO

    @CreatedDate
    private Instant criadoEm;

    public UserAthena(String nome, String email, String senha, String cargo, UserRole userRole) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
        this.role = userRole;
    }

    // ===== UserDetails =====
    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return switch (role) {
            case ADMIN -> List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_FUNCIONARIO")
            );
            case FUNCIONARIO, ESTAGIARIO -> List.of(
                    new SimpleGrantedAuthority("ROLE_FUNCIONARIO")
            );
        };
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}