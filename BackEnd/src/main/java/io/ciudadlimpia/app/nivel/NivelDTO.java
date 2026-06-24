package io.ciudadlimpia.app.nivel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NivelDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull
    @Size(max = 200)
    private String descripcion;

    @NotNull
    private Integer puntosMinimos;

    @Size(max = 100)
    private String icono;
}
