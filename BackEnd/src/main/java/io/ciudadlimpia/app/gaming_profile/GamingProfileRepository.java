package io.ciudadlimpia.app.gaming_profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GamingProfileRepository extends JpaRepository<GamingProfile, Long> {

    GamingProfile findFirstByUsuarioId(Long id);

    GamingProfile findFirstByNivelId(Integer id);

    // Nuevo — buscar por usuario directamente
    Optional<GamingProfile> findByUsuarioId(Long usuarioId);

}
