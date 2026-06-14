package io.ciudadlimpia.app.reporte;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CambiarEstadoBloqueRequest {
    private List<Long> ids;
    private String estado;
    private String comentario;
}