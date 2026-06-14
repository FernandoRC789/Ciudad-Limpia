package io.ciudadlimpia.app.transaccion_puntos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TransaccionPuntosRepository extends JpaRepository<TransaccionPuntos, Long> {

    TransaccionPuntos findFirstByGamingProfileId(Long id);

    TransaccionPuntos findFirstByReporteId(Long id);

    List<TransaccionPuntos> findByGamingProfileIdOrderByCreatedAtDesc(Long gamingProfileId);

}
