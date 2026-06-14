package io.ciudadlimpia.app.reporte;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.ciudadlimpia.app.dashboard.ZonaCalorResponse;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    Reporte findFirstByUsuarioId(Long id);

    List<Reporte> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    int countByUsuarioId(Long usuarioId);

    int countByUsuarioIdAndEstado(Long usuarioId, String estado);

    long countByEstado(String estado);
    long countByCreatedAtAfter(OffsetDateTime fecha);

    // Para zonas de calor
    @Query("SELECT new io.ciudadlimpia.app.dashboard.ZonaCalorResponse(" +
           "ROUND(r.latitud, 3), ROUND(r.longitud, 3), COUNT(r)) " +
           "FROM Reporte r " +
           "GROUP BY ROUND(r.latitud, 3), ROUND(r.longitud, 3) " +
           "ORDER BY COUNT(r) DESC")
    List<ZonaCalorResponse> getZonasCalor();

    List<Reporte> findByEstadoIn(List<String> estados);
    

}
