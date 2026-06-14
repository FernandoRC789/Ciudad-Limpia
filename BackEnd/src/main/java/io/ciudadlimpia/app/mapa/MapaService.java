package io.ciudadlimpia.app.mapa;

import io.ciudadlimpia.app.reporte.Reporte;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapaService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ReporteRepository reporteRepository;

    public MapaService(SimpMessagingTemplate messagingTemplate,
                       ReporteRepository reporteRepository) {
        this.messagingTemplate = messagingTemplate;
        this.reporteRepository = reporteRepository;
    }

    // Obtener todos los reportes activos para cargar el mapa inicial
    public List<ReporteActivoDTO> getReportesActivos() {
        return reporteRepository.findByEstadoIn(List.of("PENDIENTE", "EN_REVISION"))
                .stream()
                .map(r -> new ReporteActivoDTO(
                        r.getId(),
                        r.getLatitud(),
                        r.getLongitud(),
                        r.getEstado(),
                        r.getTitulo(),
                        "REPORTE_ACTIVO"
                ))
                .toList();
    }

    // Publicar evento cuando se crea un reporte nuevo
    public void publicarReporteNuevo(Reporte reporte) {
        ReporteActivoDTO evento = new ReporteActivoDTO(
                reporte.getId(),
                reporte.getLatitud(),
                reporte.getLongitud(),
                reporte.getEstado(),
                reporte.getTitulo(),
                "REPORTE_NUEVO"
        );
        messagingTemplate.convertAndSend("/topic/mapa", evento);
    }

    // Publicar evento cuando cambia el estado de un reporte
    public void publicarCambioEstado(Reporte reporte) {
        ReporteActivoDTO evento = new ReporteActivoDTO(
                reporte.getId(),
                reporte.getLatitud(),
                reporte.getLongitud(),
                reporte.getEstado(),
                reporte.getTitulo(),
                "ESTADO_ACTUALIZADO"
        );
        messagingTemplate.convertAndSend("/topic/mapa", evento);
    }
}