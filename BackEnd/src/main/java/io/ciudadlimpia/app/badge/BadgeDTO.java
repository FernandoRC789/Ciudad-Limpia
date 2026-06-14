package io.ciudadlimpia.app.badge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BadgeDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Size(max = 255)
    private String descripcion;

    @Size(max = 100)
    private String icono;

    @NotNull
    @Size(max = 255)
    private String condicionTipo;

    @NotNull
    private Integer condicionValor;

}
