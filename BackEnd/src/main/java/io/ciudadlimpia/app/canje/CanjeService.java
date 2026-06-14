package io.ciudadlimpia.app.canje;

import io.ciudadlimpia.app.cupon.Cupon;
import io.ciudadlimpia.app.cupon.CuponRepository;
import io.ciudadlimpia.app.events.BeforeDeleteCupon;
import io.ciudadlimpia.app.events.BeforeDeleteGamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfileRepository;
import io.ciudadlimpia.app.gaming_profile.GamingProfileService;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntos;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntosRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CanjeService {

    private final CanjeRepository canjeRepository;
    private final GamingProfileRepository gamingProfileRepository;
    private final CuponRepository cuponRepository;
    private final GamingProfileService gamingProfileService;
    private final TransaccionPuntosRepository transaccionPuntosRepository;

    public CanjeService(final CanjeRepository canjeRepository,
            final GamingProfileRepository gamingProfileRepository,
            final CuponRepository cuponRepository, GamingProfileService gamingProfileService,
            TransaccionPuntosRepository transaccionPuntosRepository) {
        this.canjeRepository = canjeRepository;
        this.gamingProfileRepository = gamingProfileRepository;
        this.cuponRepository = cuponRepository;
        this.gamingProfileService = gamingProfileService;
        this.transaccionPuntosRepository = transaccionPuntosRepository;
    }

    public List<CanjeDTO> findAll() {
        final List<Canje> canjes = canjeRepository.findAll(Sort.by("id"));
        return canjes.stream()
                .map(canje -> mapToDTO(canje, new CanjeDTO()))
                .toList();
    }

    public CanjeDTO get(final Long id) {
        return canjeRepository.findById(id)
                .map(canje -> mapToDTO(canje, new CanjeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CanjeDTO canjeDTO) {
        final Canje canje = new Canje();
        mapToEntity(canjeDTO, canje);
        return canjeRepository.save(canje).getId();
    }

    public void update(final Long id, final CanjeDTO canjeDTO) {
        final Canje canje = canjeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(canjeDTO, canje);
        canjeRepository.save(canje);
    }

    public void delete(final Long id) {
        final Canje canje = canjeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        canjeRepository.delete(canje);
    }

    private CanjeDTO mapToDTO(final Canje canje, final CanjeDTO canjeDTO) {
        canjeDTO.setId(canje.getId());
        canjeDTO.setCodigoCanje(canje.getCodigoCanje());
        canjeDTO.setEstado(canje.getEstado());
        canjeDTO.setCreatedAt(canje.getCreatedAt());
        canjeDTO.setGamingProfile(canje.getGamingProfile() == null ? null : canje.getGamingProfile().getId());
        canjeDTO.setCupon(canje.getCupon() == null ? null : canje.getCupon().getId());
        return canjeDTO;
    }

    private Canje mapToEntity(final CanjeDTO canjeDTO, final Canje canje) {
        canje.setCodigoCanje(canjeDTO.getCodigoCanje());
        canje.setEstado(canjeDTO.getEstado());
        canje.setCreatedAt(canjeDTO.getCreatedAt());
        final GamingProfile gamingProfile = canjeDTO.getGamingProfile() == null ? null
                : gamingProfileRepository.findById(canjeDTO.getGamingProfile())
                        .orElseThrow(() -> new NotFoundException("gamingProfile not found"));
        canje.setGamingProfile(gamingProfile);
        final Cupon cupon = canjeDTO.getCupon() == null ? null
                : cuponRepository.findById(canjeDTO.getCupon())
                        .orElseThrow(() -> new NotFoundException("cupon not found"));
        canje.setCupon(cupon);
        return canje;
    }

    @EventListener(BeforeDeleteGamingProfile.class)
    public void on(final BeforeDeleteGamingProfile event) {
        final ReferencedException referencedException = new ReferencedException();
        final Canje gamingProfileCanje = canjeRepository.findFirstByGamingProfileId(event.getId());
        if (gamingProfileCanje != null) {
            referencedException.setKey("gamingProfile.canje.gamingProfile.referenced");
            referencedException.addParam(gamingProfileCanje.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteCupon.class)
    public void on(final BeforeDeleteCupon event) {
        final ReferencedException referencedException = new ReferencedException();
        final Canje cuponCanje = canjeRepository.findFirstByCuponId(event.getId());
        if (cuponCanje != null) {
            referencedException.setKey("cupon.canje.cupon.referenced");
            referencedException.addParam(cuponCanje.getId());
            throw referencedException;
        }
    }

    // Canjear cupón
    public CanjeResponse canjear(Integer cuponId, Long usuarioId) {
        // Buscar cupón
        Cupon cupon = cuponRepository.findById(cuponId)
                .orElseThrow(() -> new NotFoundException("Cupón no encontrado"));

        // Validar que esté activo y tenga stock
        if (!cupon.getActivo() || cupon.getStock() <= 0) {
            throw new RuntimeException("Cupón no disponible");
        }

        // Validar fecha de expiración
        if (cupon.getFechaExpiracion() != null &&
                cupon.getFechaExpiracion().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Cupón expirado");
        }

        // Buscar gaming profile
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Perfil no encontrado"));

        // Validar puntos suficientes
        if (gp.getPuntosTotales() < cupon.getCostoPuntos()) {
            throw new RuntimeException("Puntos insuficientes");
        }

        // Descontar puntos
        gp.setPuntosTotales(gp.getPuntosTotales() - cupon.getCostoPuntos());
        gamingProfileRepository.save(gp);

        // Descontar stock
        cupon.setStock(cupon.getStock() - 1);
        cuponRepository.save(cupon);

        // Registrar transacción de puntos
        TransaccionPuntos tx = new TransaccionPuntos();
        tx.setGamingProfile(gp);
        tx.setPuntos(cupon.getCostoPuntos());
        tx.setTipo("GASTO");
        tx.setMotivo("Canje de cupón: " + cupon.getNombre());
        transaccionPuntosRepository.save(tx);

        // Crear canje con código único
        Canje canje = new Canje();
        canje.setGamingProfile(gp);
        canje.setCupon(cupon);
        canje.setCodigoCanje("CUPON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        canjeRepository.save(canje);

        return new CanjeResponse(
                canje.getId(),
                canje.getCodigoCanje(),
                cupon.getNombre(),
                cupon.getCostoPuntos(),
                gp.getPuntosTotales(),
                canje.getEstado());
    }

    // Ver mis canjes
    public List<CanjeResponse> getMisCanjes(Long usuarioId) {
        GamingProfile gp = gamingProfileRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NotFoundException("Perfil no encontrado"));

        return canjeRepository.findByGamingProfileIdOrderByCreatedAtDesc(gp.getId())
                .stream()
                .map(c -> new CanjeResponse(
                        c.getId(),
                        c.getCodigoCanje(),
                        c.getCupon().getNombre(),
                        c.getCupon().getCostoPuntos(),
                        gp.getPuntosTotales(),
                        c.getEstado()))
                .toList();
    }

}
