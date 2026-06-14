package io.ciudadlimpia.app.canje;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CanjeRepository extends JpaRepository<Canje, Long> {

    Canje findFirstByGamingProfileId(Long id);

    Canje findFirstByCuponId(Integer id);

    // Nuevos
    List<Canje> findByGamingProfileIdOrderByCreatedAtDesc(Long gamingProfileId);

}
