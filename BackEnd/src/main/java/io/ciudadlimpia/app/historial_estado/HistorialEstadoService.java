package io.ciudadlimpia.app.historial_estado;

import io.ciudadlimpia.app.events.BeforeDeleteReporte;
import io.ciudadlimpia.app.events.BeforeDeleteUsuario;
import io.ciudadlimpia.app.reporte.Reporte;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.usuario.Usuario;
import io.ciudadlimpia.app.usuario.UsuarioRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public HistorialEstadoService(final HistorialEstadoRepository historialEstadoRepository,
            final ReporteRepository reporteRepository, final UsuarioRepository usuarioRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<HistorialEstadoDTO> findAll() {
        final List<HistorialEstado> historialEstadoes = historialEstadoRepository.findAll(Sort.by("id"));
        return historialEstadoes.stream()
                .map(historialEstado -> mapToDTO(historialEstado, new HistorialEstadoDTO()))
                .toList();
    }

    public HistorialEstadoDTO get(final Long id) {
        return historialEstadoRepository.findById(id)
                .map(historialEstado -> mapToDTO(historialEstado, new HistorialEstadoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final HistorialEstadoDTO historialEstadoDTO) {
        final HistorialEstado historialEstado = new HistorialEstado();
        mapToEntity(historialEstadoDTO, historialEstado);
        return historialEstadoRepository.save(historialEstado).getId();
    }

    public void update(final Long id, final HistorialEstadoDTO historialEstadoDTO) {
        final HistorialEstado historialEstado = historialEstadoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(historialEstadoDTO, historialEstado);
        historialEstadoRepository.save(historialEstado);
    }

    public void delete(final Long id) {
        final HistorialEstado historialEstado = historialEstadoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        historialEstadoRepository.delete(historialEstado);
    }

    private HistorialEstadoDTO mapToDTO(final HistorialEstado historialEstado,
            final HistorialEstadoDTO historialEstadoDTO) {
        historialEstadoDTO.setId(historialEstado.getId());
        historialEstadoDTO.setEstadoAnterior(historialEstado.getEstadoAnterior());
        historialEstadoDTO.setEstadoNuevo(historialEstado.getEstadoNuevo());
        historialEstadoDTO.setComentario(historialEstado.getComentario());
        historialEstadoDTO.setCreatedAt(historialEstado.getCreatedAt());
        historialEstadoDTO.setReporte(historialEstado.getReporte() == null ? null : historialEstado.getReporte().getId());
        historialEstadoDTO.setAdmin(historialEstado.getAdmin() == null ? null : historialEstado.getAdmin().getId());
        return historialEstadoDTO;
    }

    private HistorialEstado mapToEntity(final HistorialEstadoDTO historialEstadoDTO,
            final HistorialEstado historialEstado) {
        historialEstado.setEstadoAnterior(historialEstadoDTO.getEstadoAnterior());
        historialEstado.setEstadoNuevo(historialEstadoDTO.getEstadoNuevo());
        historialEstado.setComentario(historialEstadoDTO.getComentario());
        historialEstado.setCreatedAt(historialEstadoDTO.getCreatedAt());
        final Reporte reporte = historialEstadoDTO.getReporte() == null ? null : reporteRepository.findById(historialEstadoDTO.getReporte())
                .orElseThrow(() -> new NotFoundException("reporte not found"));
        historialEstado.setReporte(reporte);
        final Usuario admin = historialEstadoDTO.getAdmin() == null ? null : usuarioRepository.findById(historialEstadoDTO.getAdmin())
                .orElseThrow(() -> new NotFoundException("admin not found"));
        historialEstado.setAdmin(admin);
        return historialEstado;
    }

    @EventListener(BeforeDeleteReporte.class)
    public void on(final BeforeDeleteReporte event) {
        final ReferencedException referencedException = new ReferencedException();
        final HistorialEstado reporteHistorialEstado = historialEstadoRepository.findFirstByReporteId(event.getId());
        if (reporteHistorialEstado != null) {
            referencedException.setKey("reporte.historialEstado.reporte.referenced");
            referencedException.addParam(reporteHistorialEstado.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final HistorialEstado adminHistorialEstado = historialEstadoRepository.findFirstByAdminId(event.getId());
        if (adminHistorialEstado != null) {
            referencedException.setKey("usuario.historialEstado.admin.referenced");
            referencedException.addParam(adminHistorialEstado.getId());
            throw referencedException;
        }
    }

}
