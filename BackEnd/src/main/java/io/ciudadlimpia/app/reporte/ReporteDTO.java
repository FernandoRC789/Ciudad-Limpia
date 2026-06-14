package io.ciudadlimpia.app.reporte;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReporteDTO {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String titulo;

    @NotNull
    private String descripcion;

    @NotNull
    @Digits(integer = 10, fraction = 7)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal latitud;

    @NotNull
    @Digits(integer = 10, fraction = 7)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal longitud;

    @Size(max = 255)
    private String direccion;

    @NotNull
    @Size(max = 255)
    private String estado;

    @NotNull
    private Integer puntosOtorgados;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @NotNull
    private Long usuario;

}
