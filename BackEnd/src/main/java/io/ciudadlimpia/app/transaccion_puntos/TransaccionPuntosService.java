package io.ciudadlimpia.app.transaccion_puntos;

import io.ciudadlimpia.app.events.BeforeDeleteGamingProfile;
import io.ciudadlimpia.app.events.BeforeDeleteReporte;
import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfileRepository;
import io.ciudadlimpia.app.reporte.Reporte;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TransaccionPuntosService {

    private final TransaccionPuntosRepository transaccionPuntosRepository;
    private final GamingProfileRepository gamingProfileRepository;
    private final ReporteRepository reporteRepository;

    public TransaccionPuntosService(final TransaccionPuntosRepository transaccionPuntosRepository,
            final GamingProfileRepository gamingProfileRepository,
            final ReporteRepository reporteRepository) {
        this.transaccionPuntosRepository = transaccionPuntosRepository;
        this.gamingProfileRepository = gamingProfileRepository;
        this.reporteRepository = reporteRepository;
    }

    public List<TransaccionPuntosDTO> findAll() {
        final List<TransaccionPuntos> transaccionPuntoses = transaccionPuntosRepository.findAll(Sort.by("id"));
        return transaccionPuntoses.stream()
                .map(transaccionPuntos -> mapToDTO(transaccionPuntos, new TransaccionPuntosDTO()))
                .toList();
    }

    public TransaccionPuntosDTO get(final Long id) {
        return transaccionPuntosRepository.findById(id)
                .map(transaccionPuntos -> mapToDTO(transaccionPuntos, new TransaccionPuntosDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TransaccionPuntosDTO transaccionPuntosDTO) {
        final TransaccionPuntos transaccionPuntos = new TransaccionPuntos();
        mapToEntity(transaccionPuntosDTO, transaccionPuntos);
        return transaccionPuntosRepository.save(transaccionPuntos).getId();
    }

    public void update(final Long id, final TransaccionPuntosDTO transaccionPuntosDTO) {
        final TransaccionPuntos transaccionPuntos = transaccionPuntosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(transaccionPuntosDTO, transaccionPuntos);
        transaccionPuntosRepository.save(transaccionPuntos);
    }

    public void delete(final Long id) {
        final TransaccionPuntos transaccionPuntos = transaccionPuntosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        transaccionPuntosRepository.delete(transaccionPuntos);
    }

    private TransaccionPuntosDTO mapToDTO(final TransaccionPuntos transaccionPuntos,
            final TransaccionPuntosDTO transaccionPuntosDTO) {
        transaccionPuntosDTO.setId(transaccionPuntos.getId());
        transaccionPuntosDTO.setPuntos(transaccionPuntos.getPuntos());
        transaccionPuntosDTO.setTipo(transaccionPuntos.getTipo());
        transaccionPuntosDTO.setMotivo(transaccionPuntos.getMotivo());
        transaccionPuntosDTO.setCreatedAt(transaccionPuntos.getCreatedAt());
        transaccionPuntosDTO.setGamingProfile(transaccionPuntos.getGamingProfile() == null ? null : transaccionPuntos.getGamingProfile().getId());
        transaccionPuntosDTO.setReporte(transaccionPuntos.getReporte() == null ? null : transaccionPuntos.getReporte().getId());
        return transaccionPuntosDTO;
    }

    private TransaccionPuntos mapToEntity(final TransaccionPuntosDTO transaccionPuntosDTO,
            final TransaccionPuntos transaccionPuntos) {
        transaccionPuntos.setPuntos(transaccionPuntosDTO.getPuntos());
        transaccionPuntos.setTipo(transaccionPuntosDTO.getTipo());
        transaccionPuntos.setMotivo(transaccionPuntosDTO.getMotivo());
        transaccionPuntos.setCreatedAt(transaccionPuntosDTO.getCreatedAt());
        final GamingProfile gamingProfile = transaccionPuntosDTO.getGamingProfile() == null ? null : gamingProfileRepository.findById(transaccionPuntosDTO.getGamingProfile())
                .orElseThrow(() -> new NotFoundException("gamingProfile not found"));
        transaccionPuntos.setGamingProfile(gamingProfile);
        final Reporte reporte = transaccionPuntosDTO.getReporte() == null ? null : reporteRepository.findById(transaccionPuntosDTO.getReporte())
                .orElseThrow(() -> new NotFoundException("reporte not found"));
        transaccionPuntos.setReporte(reporte);
        return transaccionPuntos;
    }

    @EventListener(BeforeDeleteGamingProfile.class)
    public void on(final BeforeDeleteGamingProfile event) {
        final ReferencedException referencedException = new ReferencedException();
        final TransaccionPuntos gamingProfileTransaccionPuntos = transaccionPuntosRepository.findFirstByGamingProfileId(event.getId());
        if (gamingProfileTransaccionPuntos != null) {
            referencedException.setKey("gamingProfile.transaccionPuntos.gamingProfile.referenced");
            referencedException.addParam(gamingProfileTransaccionPuntos.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteReporte.class)
    public void on(final BeforeDeleteReporte event) {
        final ReferencedException referencedException = new ReferencedException();
        final TransaccionPuntos reporteTransaccionPuntos = transaccionPuntosRepository.findFirstByReporteId(event.getId());
        if (reporteTransaccionPuntos != null) {
            referencedException.setKey("reporte.transaccionPuntos.reporte.referenced");
            referencedException.addParam(reporteTransaccionPuntos.getId());
            throw referencedException;
        }
    }

}
