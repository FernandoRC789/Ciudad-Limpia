package io.ciudadlimpia.app.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipo;
    private String rol;
    private Long usuarioId;
}