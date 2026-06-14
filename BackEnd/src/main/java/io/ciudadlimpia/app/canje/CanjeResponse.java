package io.ciudadlimpia.app.canje;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CanjeResponse {
    private Long id;
    private String codigoCanje;
    private String cuponNombre;
    private Integer puntosGastados;
    private Integer puntosRestantes;
    private String estado;
}