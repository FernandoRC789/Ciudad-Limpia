package io.ciudadlimpia.app.transaccion_puntos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransaccionPuntosDTO {

    private Long id;

    @NotNull
    private Integer puntos;

    @NotNull
    @Size(max = 255)
    private String tipo;

    @NotNull
    @Size(max = 150)
    private String motivo;

    private OffsetDateTime createdAt;

    @NotNull
    private Long gamingProfile;

    private Long reporte;

}
