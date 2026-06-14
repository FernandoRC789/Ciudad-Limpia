package io.ciudadlimpia.app.historial_estado;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HistorialEstadoDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String estadoAnterior;

    @NotNull
    @Size(max = 255)
    private String estadoNuevo;

    private String comentario;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private Long reporte;

    @NotNull
    private Long admin;

}
