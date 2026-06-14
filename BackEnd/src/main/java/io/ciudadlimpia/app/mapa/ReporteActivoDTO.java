package io.ciudadlimpia.app.mapa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReporteActivoDTO {
    private Long reporteId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String estado;
    private String titulo;
    private String evento; // REPORTE_NUEVO o ESTADO_ACTUALIZADO
}