package io.ciudadlimpia.app.rol;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RolDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @Size(max = 200)
    private String descripcion;

}
