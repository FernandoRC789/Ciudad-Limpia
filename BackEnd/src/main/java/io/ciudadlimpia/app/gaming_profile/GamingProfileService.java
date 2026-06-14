package io.ciudadlimpia.app.gaming_profile;

import io.ciudadlimpia.app.badge.Badge;
import io.ciudadlimpia.app.badge.BadgeRepository;
import io.ciudadlimpia.app.events.BeforeDeleteGamingProfile;
import io.ciudadlimpia.app.events.BeforeDeleteNivel;
import io.ciudadlimpia.app.events.BeforeDeleteUsuario;
import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadge;
import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadgeDTO;
import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadgeRepository;
import io.ciudadlimpia.app.nivel.Nivel;
import io.ciudadlimpia.app.nivel.NivelRepository;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntos;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntosDTO;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntosRepository;
import io.ciudadlimpia.app.usuario.Usuario;
import io.ciudadlimpia.app.usuario.UsuarioRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GamingProfileService {

    private final GamingProfileRepository gamingProfileRepository;
    private final UsuarioRepository usuarioRepository;
    private final NivelRepository nivelRepository;
    private final ApplicationEventPublisher publisher;
    private final ReporteRepository reporteRepository;
    private final GamingProfileBadgeRepository gamingProfileBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final TransaccionPuntosRepository transaccionPuntosRepository;

    public GamingProfileService(final GamingProfileRepository gamingProfileRepository,
            final UsuarioRepository usuarioRepository, final NivelRepository nivelRepository,
            final ApplicationEventPublisher publisher, ReporteRepository reporteRepository,
            GamingProfileBadgeRepository gamingProfileBadgeRepository, BadgeRepository badgeRepository,
            TransaccionPuntosRepository transaccionPuntosRepository) {
        this.gamingProfileRepository = gamingProfileRepository;
        this.usuarioRepository = usuarioRepository;
        this.nivelRepository = nivelRepository;
        this.publisher = publisher;
        this.reporteRepository = reporteRepository;
        this.gamingProfileBadgeRepository = gamingProfileBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.transaccionPuntosRepository = transaccionPuntosRepository;
    }

    public List<GamingProfileDTO> findAll() {
        final List<GamingProfile> gamingProfiles = gamingProfileRepository.findAll(Sort.by("id"));
        return gamingProfiles.stream()
                .map(gamingProfile -> mapToDTO(gamingProfile, new GamingProfileDTO()))
                .toList();
    }

    public GamingProfileDTO get(final Long id) {
        return gamingProfileRepository.findById(id)
                .map(gamingProfile -> mapToDTO(gamingProfile, new GamingProfileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GamingProfileDTO gamingProfileDTO) {
        final GamingProfile gamingProfile = new GamingProfile();
        mapToEntity(gamingProfileDTO, gamingProfile);
        return gamingProfileRepository.save(gamingProfile).getId();
    }

    public void update(final Long id, final GamingProfileDTO gamingProfileDTO) {
        final GamingProfile gamingProfile = gamingProfileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gamingProfileDTO, gamingProfile);
        gamingProfileRepository.save(gamingProfile);
    }

    public void delete(final Long id) {
        final GamingProfile gamingProfile = gamingProfileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGamingProfile(id));
        gamingProfileRepository.delete(gamingProfile);
    }

    private GamingProfileDTO mapToDTO(final GamingProfile gamingProfile,
            final GamingProfileDTO gamingProfileDTO) {
        gamingProfileDTO.setId(gamingProfile.getId());
        gamingProfileDTO.setPuntosTotales(gamingProfile.getPuntosTotales());
        gamingProfileDTO.setCreatedAt(gamingProfile.getCreatedAt());
        gamingProfileDTO.setUpdatedAt(gamingProfile.getUpdatedAt());
        gamingProfileDTO.setUsuario(gamingProfile.getUsuario() == null ? null : gamingProfile.getUsuario().getId());
        gamingProfileDTO.setNivel(gamingProfile.getNivel() == null ? null : gamingProfile.getNivel().getId());
        return gamingProfileDTO;
    }

    private GamingProfile mapToEntity(final GamingProfileDTO gamingProfileDTO,
            final GamingProfile gamingProfile) {
        gamingProfile.setPuntosTotales(gamingProfileDTO.getPuntosTotales());
        gamingProfile.setCreatedAt(gamingProfileDTO.getCreatedAt());
        gamingProfile.setUpdatedAt(gamingProfileDTO.getUpdatedAt());
        final Usuario usuario = gamingProfileDTO.getUsuario() == null ? null
                : usuarioRepository.findById(gamingProfileDTO.getUsuario())
                        .orElseThrow(() -> new NotFoundException("usuario not found"));
        gamingProfile.setUsuario(usuario);
        final Nivel nivel = gamingProfileDTO.getNivel() == null ? null
                : nivelRepository.findById(gamingProfileDTO.getNivel())
                        .orElseThrow(() -> new NotFoundException("nivel not found"));
        gamingProfile.setNivel(nivel);
        return gamingProfile;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final GamingProfile usuarioGamingProfile = gamingProfileRepository.findFirstByUsuarioId(event.getId());
        if (usuarioGamingProfile != null) {
            referencedException.setKey("usuario.gamingProfile.usuario.referenced");
            referencedException.addParam(usuarioGamingProfile.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteNivel.class)
    public void on(final BeforeDeleteNivel event) {
        final ReferencedException referencedException = new ReferencedException();
        final GamingProfile nivelGamingProfile = gamingProfileRepository.findFirstByNivelId(event.getId());
        if (nivelGamingProfile != null) {
            referencedException.setKey("nivel.gamingProfile.nivel.referenced");
            referencedException.addParam(nivelGamingProfile.getId());
            throw referencedException;
        }
    }

    // Ver perfil completo del ciudadano
    public PerfilResponse getPerfil(Long usuarioId) {
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Gaming profile no encontrado"));

        Usuario usuario = gp.getUsuario();
        Nivel nivelActual = gp.getNivel();

        // Calcular puntos para siguiente nivel
        List<Nivel> niveles = nivelRepository.findAll(Sort.by("puntosMinimos"));
        Integer puntosParaSiguiente = niveles.stream()
                .filter(n -> n.getPuntosMinimos() > gp.getPuntosTotales())
                .findFirst()
                .map(n -> n.getPuntosMinimos() - gp.getPuntosTotales())
                .orElse(0);

        // Contar reportes
        int totalReportes = reporteRepository.countByUsuarioId(usuarioId);
        int reportesAtendidos = reporteRepository.countByUsuarioIdAndEstado(usuarioId, "ATENDIDO");

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getFotoPerfil(),
                gp.getPuntosTotales(),
                new PerfilResponse.NivelInfo(nivelActual.getNombre(), nivelActual.getIcono(), puntosParaSiguiente),
                totalReportes,
                reportesAtendidos);
    }

    // Ver badges del ciudadano
    public List<GamingProfileBadgeDTO> getMisBadges(Long usuarioId) {
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Gaming profile no encontrado"));
        return gamingProfileBadgeRepository.findByGamingProfileId(gp.getId())
                .stream()
                .map(gpb -> mapBadgeToDTO(gpb))
                .toList();
    }

    private GamingProfileBadgeDTO mapBadgeToDTO(GamingProfileBadge gpb) {
        GamingProfileBadgeDTO dto = new GamingProfileBadgeDTO();
        dto.setId(gpb.getId());
        dto.setObtenidoAt(gpb.getObtenidoAt());
        dto.setBadge(gpb.getBadge().getId());
        dto.setGamingProfile(gpb.getGamingProfile().getId());
        return dto;
    }

    // Otorgar puntos y verificar nivel y badges
    public void otorgarPuntos(Long usuarioId, int puntos, Long reporteId) {
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Gaming profile no encontrado"));

        // Sumar puntos
        gp.setPuntosTotales(gp.getPuntosTotales() + puntos);

        // Verificar si sube de nivel
        List<Nivel> niveles = nivelRepository.findAll(Sort.by("puntosMinimos"));
        niveles.stream()
                .filter(n -> n.getPuntosMinimos() <= gp.getPuntosTotales())
                .reduce((first, second) -> second) // el último que cumple la condición
                .ifPresent(gp::setNivel);

        gamingProfileRepository.save(gp);

        // Registrar transacción
        TransaccionPuntos tx = new TransaccionPuntos();
        tx.setGamingProfile(gp);
        tx.setPuntos(puntos);
        tx.setTipo("GANANCIA");
        tx.setMotivo("Reporte atendido");
        if (reporteId != null) {
            reporteRepository.findById(reporteId).ifPresent(tx::setReporte);
        }
        transaccionPuntosRepository.save(tx);

        // Verificar badges
        verificarBadges(gp, usuarioId);
    }

    private void verificarBadges(GamingProfile gp, Long usuarioId) {
        List<Badge> badges = badgeRepository.findAll();
        int totalReportes = reporteRepository.countByUsuarioId(usuarioId);
        int reportesAtendidos = reporteRepository.countByUsuarioIdAndEstado(usuarioId, "ATENDIDO");

        for (Badge badge : badges) {
            // Si ya tiene el badge, saltar
            if (gamingProfileBadgeRepository.existsByGamingProfileIdAndBadgeId(gp.getId(), badge.getId())) {
                continue;
            }

            boolean ganaBadge = switch (badge.getCondicionTipo()) {
                case "PRIMER_REPORTE" -> totalReportes >= 1;
                case "REPORTES_ENVIADOS" -> totalReportes >= badge.getCondicionValor();
                case "REPORTES_ATENDIDOS" -> reportesAtendidos >= badge.getCondicionValor();
                case "PUNTOS_ACUMULADOS" -> gp.getPuntosTotales() >= badge.getCondicionValor();
                default -> false;
            };

            if (ganaBadge) {
                GamingProfileBadge gpb = new GamingProfileBadge();
                gpb.setGamingProfile(gp);
                gpb.setBadge(badge);
                gamingProfileBadgeRepository.save(gpb);
            }
        }
    }

    public List<TransaccionPuntosDTO> getHistorialPuntos(Long usuarioId) {
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Perfil no encontrado"));

        return transaccionPuntosRepository
                .findByGamingProfileIdOrderByCreatedAtDesc(gp.getId())
                .stream()
                .map(tx -> {
                    TransaccionPuntosDTO dto = new TransaccionPuntosDTO();
                    dto.setId(tx.getId());
                    dto.setPuntos(tx.getPuntos());
                    dto.setTipo(tx.getTipo());
                    dto.setMotivo(tx.getMotivo());
                    dto.setCreatedAt(tx.getCreatedAt());
                    dto.setGamingProfile(gp.getId());
                    dto.setReporte(tx.getReporte() == null ? null : tx.getReporte().getId());
                    return dto;
                })
                .toList();
    }

    public List<RankingResponse> getRanking() {
        List<GamingProfile> perfiles = gamingProfileRepository
                .findAll(Sort.by(Sort.Direction.DESC, "puntosTotales"))
                .stream()
                .filter(gp -> gp.getUsuario().getActivo() &&
                        gp.getUsuario().getRol().getNombre().equals("ROLE_CIUDADANO"))
                .toList();

        List<RankingResponse> ranking = new ArrayList<>();
        for (int i = 0; i < perfiles.size(); i++) {
            GamingProfile gp = perfiles.get(i);
            int totalReportes = reporteRepository.countByUsuarioId(gp.getUsuario().getId());
            ranking.add(new RankingResponse(
                    i + 1,
                    gp.getUsuario().getNombre() + " " + gp.getUsuario().getApellido(),
                    gp.getNivel().getNombre(),
                    gp.getPuntosTotales(),
                    totalReportes));
        }
        return ranking;
    }

}
