package io.ciudadlimpia.app.reporte;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CrearReporteRequest {

    @NotNull
    @Size(max = 150)
    private String titulo;

    @NotNull
    private String descripcion;

    @NotNull
    @Digits(integer = 10, fraction = 7)
    private BigDecimal latitud;

    @NotNull
    @Digits(integer = 10, fraction = 7)
    private BigDecimal longitud;

    @Size(max = 255)
    private String direccion;
}