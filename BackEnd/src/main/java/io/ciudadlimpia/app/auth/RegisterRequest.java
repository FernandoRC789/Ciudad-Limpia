package io.ciudadlimpia.app.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
}