package io.ciudadlimpia.app.historial_estado;

import org.springframework.data.jpa.repository.JpaRepository;


public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    HistorialEstado findFirstByReporteId(Long id);

    HistorialEstado findFirstByAdminId(Long id);

}
