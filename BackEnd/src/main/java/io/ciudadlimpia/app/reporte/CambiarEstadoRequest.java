package io.ciudadlimpia.app.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoRequest {
    private String estado;
    private String comentario;
}