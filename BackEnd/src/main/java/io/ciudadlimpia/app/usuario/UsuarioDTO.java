package io.ciudadlimpia.app.usuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioDTO {

    private Long id;

    @NotNull
    @Size(max = 8)
    private String dni;

    @NotNull
    @Size(max = 80)
    private String nombre;

    @NotNull
    @Size(max = 80)
    private String apellido;

    @NotNull
    @Size(max = 150)
    private String email;

    @NotNull
    @JsonIgnore
    @Size(max = 255)
    private String passwordHash;

    @Size(max = 20)
    private String telefono;

    @Size(max = 255)
    private String fotoPerfil;

    @NotNull
    private Boolean activo;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String direccion;

    @Size(max = 255)
    private String distrito;

    @NotNull
    private Integer rol;

}
