package io.ciudadlimpia.app.gaming_profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PerfilResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String fotoPerfil;
    private Integer puntosTotal;
    private NivelInfo nivel;
    private Integer totalReportes;
    private Integer reportesAtendidos;

    @Getter
    @AllArgsConstructor
    public static class NivelInfo {
        private String nombre;
        private String icono;
        private Integer puntosParaSiguienteNivel;
    }
}