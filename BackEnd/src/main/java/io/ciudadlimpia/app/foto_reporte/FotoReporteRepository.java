package io.ciudadlimpia.app.foto_reporte;

import org.springframework.data.jpa.repository.JpaRepository;


public interface FotoReporteRepository extends JpaRepository<FotoReporte, Long> {

    FotoReporte findFirstByReporteId(Long id);

}
