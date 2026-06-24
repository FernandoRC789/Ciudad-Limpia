package io.ciudadlimpia.app.perfil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
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
    private String siguienteNivel;
    private Integer totalReportes;
    private Integer reportesAtendidos;

    @Getter
    @AllArgsConstructor
    public static class NivelInfo {
        private Integer id;
        private String nombre;
        private String icono;
        private Integer puntosParaSiguienteNivel;
    }
}
