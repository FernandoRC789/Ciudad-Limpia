package io.ciudadlimpia.app.reporte;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CambiarEstadoBloqueResponse {
    private int reportesActualizados;
    private String estadoNuevo;
}