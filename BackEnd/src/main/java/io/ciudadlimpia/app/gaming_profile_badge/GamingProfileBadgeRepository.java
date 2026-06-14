package io.ciudadlimpia.app.gaming_profile_badge;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GamingProfileBadgeRepository extends JpaRepository<GamingProfileBadge, Long> {

    GamingProfileBadge findFirstByGamingProfileId(Long id);

    GamingProfileBadge findFirstByBadgeId(Integer id);

    // Nuevos
    List<GamingProfileBadge> findByGamingProfileId(Long gamingProfileId);
    boolean existsByGamingProfileIdAndBadgeId(Long gamingProfileId, Integer badgeId);

}
