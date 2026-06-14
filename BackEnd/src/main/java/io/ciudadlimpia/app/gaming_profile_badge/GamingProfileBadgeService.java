package io.ciudadlimpia.app.gaming_profile_badge;

import io.ciudadlimpia.app.badge.Badge;
import io.ciudadlimpia.app.badge.BadgeRepository;
import io.ciudadlimpia.app.events.BeforeDeleteBadge;
import io.ciudadlimpia.app.events.BeforeDeleteGamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfileRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GamingProfileBadgeService {

    private final GamingProfileBadgeRepository gamingProfileBadgeRepository;
    private final GamingProfileRepository gamingProfileRepository;
    private final BadgeRepository badgeRepository;

    public GamingProfileBadgeService(
            final GamingProfileBadgeRepository gamingProfileBadgeRepository,
            final GamingProfileRepository gamingProfileRepository,
            final BadgeRepository badgeRepository) {
        this.gamingProfileBadgeRepository = gamingProfileBadgeRepository;
        this.gamingProfileRepository = gamingProfileRepository;
        this.badgeRepository = badgeRepository;
    }

    public List<GamingProfileBadgeDTO> findAll() {
        final List<GamingProfileBadge> gamingProfileBadges = gamingProfileBadgeRepository.findAll(Sort.by("id"));
        return gamingProfileBadges.stream()
                .map(gamingProfileBadge -> mapToDTO(gamingProfileBadge, new GamingProfileBadgeDTO()))
                .toList();
    }

    public GamingProfileBadgeDTO get(final Long id) {
        return gamingProfileBadgeRepository.findById(id)
                .map(gamingProfileBadge -> mapToDTO(gamingProfileBadge, new GamingProfileBadgeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GamingProfileBadgeDTO gamingProfileBadgeDTO) {
        final GamingProfileBadge gamingProfileBadge = new GamingProfileBadge();
        mapToEntity(gamingProfileBadgeDTO, gamingProfileBadge);
        return gamingProfileBadgeRepository.save(gamingProfileBadge).getId();
    }

    public void update(final Long id, final GamingProfileBadgeDTO gamingProfileBadgeDTO) {
        final GamingProfileBadge gamingProfileBadge = gamingProfileBadgeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gamingProfileBadgeDTO, gamingProfileBadge);
        gamingProfileBadgeRepository.save(gamingProfileBadge);
    }

    public void delete(final Long id) {
        final GamingProfileBadge gamingProfileBadge = gamingProfileBadgeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        gamingProfileBadgeRepository.delete(gamingProfileBadge);
    }

    private GamingProfileBadgeDTO mapToDTO(final GamingProfileBadge gamingProfileBadge,
            final GamingProfileBadgeDTO gamingProfileBadgeDTO) {
        gamingProfileBadgeDTO.setId(gamingProfileBadge.getId());
        gamingProfileBadgeDTO.setObtenidoAt(gamingProfileBadge.getObtenidoAt());
        gamingProfileBadgeDTO.setGamingProfile(gamingProfileBadge.getGamingProfile() == null ? null : gamingProfileBadge.getGamingProfile().getId());
        gamingProfileBadgeDTO.setBadge(gamingProfileBadge.getBadge() == null ? null : gamingProfileBadge.getBadge().getId());
        return gamingProfileBadgeDTO;
    }

    private GamingProfileBadge mapToEntity(final GamingProfileBadgeDTO gamingProfileBadgeDTO,
            final GamingProfileBadge gamingProfileBadge) {
        gamingProfileBadge.setObtenidoAt(gamingProfileBadgeDTO.getObtenidoAt());
        final GamingProfile gamingProfile = gamingProfileBadgeDTO.getGamingProfile() == null ? null : gamingProfileRepository.findById(gamingProfileBadgeDTO.getGamingProfile())
                .orElseThrow(() -> new NotFoundException("gamingProfile not found"));
        gamingProfileBadge.setGamingProfile(gamingProfile);
        final Badge badge = gamingProfileBadgeDTO.getBadge() == null ? null : badgeRepository.findById(gamingProfileBadgeDTO.getBadge())
                .orElseThrow(() -> new NotFoundException("badge not found"));
        gamingProfileBadge.setBadge(badge);
        return gamingProfileBadge;
    }

    @EventListener(BeforeDeleteGamingProfile.class)
    public void on(final BeforeDeleteGamingProfile event) {
        final ReferencedException referencedException = new ReferencedException();
        final GamingProfileBadge gamingProfileGamingProfileBadge = gamingProfileBadgeRepository.findFirstByGamingProfileId(event.getId());
        if (gamingProfileGamingProfileBadge != null) {
            referencedException.setKey("gamingProfile.gamingProfileBadge.gamingProfile.referenced");
            referencedException.addParam(gamingProfileGamingProfileBadge.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteBadge.class)
    public void on(final BeforeDeleteBadge event) {
        final ReferencedException referencedException = new ReferencedException();
        final GamingProfileBadge badgeGamingProfileBadge = gamingProfileBadgeRepository.findFirstByBadgeId(event.getId());
        if (badgeGamingProfileBadge != null) {
            referencedException.setKey("badge.gamingProfileBadge.badge.referenced");
            referencedException.addParam(badgeGamingProfileBadge.getId());
            throw referencedException;
        }
    }

}
