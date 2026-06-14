package io.ciudadlimpia.app.cupon;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CuponDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Size(max = 255)
    private String descripcion;

    @NotNull
    private Integer costoPuntos;

    @NotNull
    private Integer stock;

    private OffsetDateTime fechaExpiracion;

    @NotNull
    private Boolean activo;

    private OffsetDateTime createdAt;

}
