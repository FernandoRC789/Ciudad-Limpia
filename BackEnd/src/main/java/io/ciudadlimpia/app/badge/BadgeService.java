package io.ciudadlimpia.app.badge;

import io.ciudadlimpia.app.events.BeforeDeleteBadge;
import io.ciudadlimpia.app.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final ApplicationEventPublisher publisher;

    public BadgeService(final BadgeRepository badgeRepository,
            final ApplicationEventPublisher publisher) {
        this.badgeRepository = badgeRepository;
        this.publisher = publisher;
    }

    public List<BadgeDTO> findAll() {
        final List<Badge> badges = badgeRepository.findAll(Sort.by("id"));
        return badges.stream()
                .map(badge -> mapToDTO(badge, new BadgeDTO()))
                .toList();
    }

    public BadgeDTO get(final Integer id) {
        return badgeRepository.findById(id)
                .map(badge -> mapToDTO(badge, new BadgeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BadgeDTO badgeDTO) {
        final Badge badge = new Badge();
        mapToEntity(badgeDTO, badge);
        return badgeRepository.save(badge).getId();
    }

    public void update(final Integer id, final BadgeDTO badgeDTO) {
        final Badge badge = badgeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(badgeDTO, badge);
        badgeRepository.save(badge);
    }

    public void delete(final Integer id) {
        final Badge badge = badgeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteBadge(id));
        badgeRepository.delete(badge);
    }

    private BadgeDTO mapToDTO(final Badge badge, final BadgeDTO badgeDTO) {
        badgeDTO.setId(badge.getId());
        badgeDTO.setNombre(badge.getNombre());
        badgeDTO.setDescripcion(badge.getDescripcion());
        badgeDTO.setIcono(badge.getIcono());
        badgeDTO.setCondicionTipo(badge.getCondicionTipo());
        badgeDTO.setCondicionValor(badge.getCondicionValor());
        return badgeDTO;
    }

    private Badge mapToEntity(final BadgeDTO badgeDTO, final Badge badge) {
        badge.setNombre(badgeDTO.getNombre());
        badge.setDescripcion(badgeDTO.getDescripcion());
        badge.setIcono(badgeDTO.getIcono());
        badge.setCondicionTipo(badgeDTO.getCondicionTipo());
        badge.setCondicionValor(badgeDTO.getCondicionValor());
        return badge;
    }

}
