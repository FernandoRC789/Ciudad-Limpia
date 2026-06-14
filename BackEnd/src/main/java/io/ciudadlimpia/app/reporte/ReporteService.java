package io.ciudadlimpia.app.reporte;

import io.ciudadlimpia.app.events.BeforeDeleteReporte;
import io.ciudadlimpia.app.events.BeforeDeleteUsuario;
import io.ciudadlimpia.app.gaming_profile.GamingProfileService;
import io.ciudadlimpia.app.historial_estado.HistorialEstado;
import io.ciudadlimpia.app.historial_estado.HistorialEstadoRepository;
import io.ciudadlimpia.app.mapa.MapaService;
import io.ciudadlimpia.app.usuario.Usuario;
import io.ciudadlimpia.app.usuario.UsuarioRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final ApplicationEventPublisher publisher;
    private final GamingProfileService gamingProfileService;
    private final MapaService  mapaService;

    public ReporteService(final ReporteRepository reporteRepository,
            final HistorialEstadoRepository historialEstadoRepository,
            final UsuarioRepository usuarioRepository, final ApplicationEventPublisher publisher,
            GamingProfileService gamingProfileService, MapaService mapaService) {
        this.reporteRepository = reporteRepository;
        this.historialEstadoRepository = historialEstadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.publisher = publisher;
        this.gamingProfileService = gamingProfileService;
        this.mapaService = mapaService;
    }

    public List<ReporteDTO> findAll() {
        final List<Reporte> reportes = reporteRepository.findAll(Sort.by("id"));
        return reportes.stream()
                .map(reporte -> mapToDTO(reporte, new ReporteDTO()))
                .toList();
    }

    public ReporteDTO get(final Long id) {
        return reporteRepository.findById(id)
                .map(reporte -> mapToDTO(reporte, new ReporteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ReporteDTO reporteDTO) {
        final Reporte reporte = new Reporte();
        mapToEntity(reporteDTO, reporte);
        return reporteRepository.save(reporte).getId();
    }

    public void update(final Long id, final ReporteDTO reporteDTO) {
        final Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(reporteDTO, reporte);
        reporteRepository.save(reporte);
    }

    public void delete(final Long id) {
        final Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteReporte(id));
        reporteRepository.delete(reporte);
    }

    private ReporteDTO mapToDTO(final Reporte reporte, final ReporteDTO reporteDTO) {
        reporteDTO.setId(reporte.getId());
        reporteDTO.setTitulo(reporte.getTitulo());
        reporteDTO.setDescripcion(reporte.getDescripcion());
        reporteDTO.setLatitud(reporte.getLatitud());
        reporteDTO.setLongitud(reporte.getLongitud());
        reporteDTO.setDireccion(reporte.getDireccion());
        reporteDTO.setEstado(reporte.getEstado());
        reporteDTO.setPuntosOtorgados(reporte.getPuntosOtorgados());
        reporteDTO.setCreatedAt(reporte.getCreatedAt());
        reporteDTO.setUpdatedAt(reporte.getUpdatedAt());
        reporteDTO.setUsuario(reporte.getUsuario() == null ? null : reporte.getUsuario().getId());
        return reporteDTO;
    }

    private Reporte mapToEntity(final ReporteDTO reporteDTO, final Reporte reporte) {
        reporte.setTitulo(reporteDTO.getTitulo());
        reporte.setDescripcion(reporteDTO.getDescripcion());
        reporte.setLatitud(reporteDTO.getLatitud());
        reporte.setLongitud(reporteDTO.getLongitud());
        reporte.setDireccion(reporteDTO.getDireccion());
        reporte.setEstado(reporteDTO.getEstado());
        reporte.setPuntosOtorgados(reporteDTO.getPuntosOtorgados());
        reporte.setCreatedAt(reporteDTO.getCreatedAt());
        reporte.setUpdatedAt(reporteDTO.getUpdatedAt());
        final Usuario usuario = reporteDTO.getUsuario() == null ? null
                : usuarioRepository.findById(reporteDTO.getUsuario())
                        .orElseThrow(() -> new NotFoundException("usuario not found"));
        reporte.setUsuario(usuario);
        return reporte;
    }

    @EventListener(BeforeDeleteUsuario.class)
    public void on(final BeforeDeleteUsuario event) {
        final ReferencedException referencedException = new ReferencedException();
        final Reporte usuarioReporte = reporteRepository.findFirstByUsuarioId(event.getId());
        if (usuarioReporte != null) {
            referencedException.setKey("usuario.reporte.usuario.referenced");
            referencedException.addParam(usuarioReporte.getId());
            throw referencedException;
        }
    }

    // Crear reporte tomando el usuario del token
    public ReporteDTO createForCiudadano(CrearReporteRequest request, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("usuario not found"));

        Reporte reporte = new Reporte();
        reporte.setTitulo(request.getTitulo());
        reporte.setDescripcion(request.getDescripcion());
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        reporte.setDireccion(request.getDireccion());
        reporte.setUsuario(usuario);

        reporteRepository.save(reporte);
        mapaService.publicarReporteNuevo(reporte); // ← agrega esta línea
        return mapToDTO(reporte, new ReporteDTO());
    }

    // Mis reportes
    public List<ReporteDTO> findByUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId)
                .stream()
                .map(reporte -> mapToDTO(reporte, new ReporteDTO()))
                .toList();
    }

    // Cambiar estado individual
    public void cambiarEstado(Long reporteId, String nuevoEstado, String comentario, Long adminId) {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(NotFoundException::new);

        String estadoAnterior = reporte.getEstado();
        reporte.setEstado(nuevoEstado);

        // Otorgar puntos si pasa a ATENDIDO
        if ("ATENDIDO".equals(nuevoEstado) && !"ATENDIDO".equals(estadoAnterior)) {
            int puntos = 50; // puntos por reporte atendido
            reporte.setPuntosOtorgados(puntos);
            gamingProfileService.otorgarPuntos(reporte.getUsuario().getId(), puntos, reporteId);
        }

        reporteRepository.save(reporte);
        mapaService.publicarCambioEstado(reporte); // ← agrega esta línea

        // Historial
        HistorialEstado historial = new HistorialEstado();
        historial.setReporte(reporte);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado);
        historial.setComentario(comentario);
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("admin not found"));
        historial.setAdmin(admin);
        historialEstadoRepository.save(historial);
    }

    // Cambiar estado en bloque
    public int cambiarEstadoEnBloque(List<Long> ids, String nuevoEstado, String comentario, Long adminId) {
        List<Reporte> reportes = reporteRepository.findAllById(ids);
        reportes.forEach(r -> cambiarEstado(r.getId(), nuevoEstado, comentario, adminId));
        return reportes.size();
    }

}
