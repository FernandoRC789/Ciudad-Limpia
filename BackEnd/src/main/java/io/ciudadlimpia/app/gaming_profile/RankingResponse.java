package io.ciudadlimpia.app.gaming_profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponse {
    private Integer posicion;
    private String nombreCompleto;
    private String nivel;
    private Integer puntosTotal;
    private Integer totalReportes;
}