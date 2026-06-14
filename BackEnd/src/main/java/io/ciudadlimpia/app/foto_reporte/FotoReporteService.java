package io.ciudadlimpia.app.foto_reporte;

import io.ciudadlimpia.app.events.BeforeDeleteReporte;
import io.ciudadlimpia.app.reporte.Reporte;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class FotoReporteService {

    private final FotoReporteRepository fotoReporteRepository;
    private final ReporteRepository reporteRepository;

    public FotoReporteService(final FotoReporteRepository fotoReporteRepository,
            final ReporteRepository reporteRepository) {
        this.fotoReporteRepository = fotoReporteRepository;
        this.reporteRepository = reporteRepository;
    }

    public List<FotoReporteDTO> findAll() {
        final List<FotoReporte> fotoReportes = fotoReporteRepository.findAll(Sort.by("id"));
        return fotoReportes.stream()
                .map(fotoReporte -> mapToDTO(fotoReporte, new FotoReporteDTO()))
                .toList();
    }

    public FotoReporteDTO get(final Long id) {
        return fotoReporteRepository.findById(id)
                .map(fotoReporte -> mapToDTO(fotoReporte, new FotoReporteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FotoReporteDTO fotoReporteDTO) {
        final FotoReporte fotoReporte = new FotoReporte();
        mapToEntity(fotoReporteDTO, fotoReporte);
        return fotoReporteRepository.save(fotoReporte).getId();
    }

    public void update(final Long id, final FotoReporteDTO fotoReporteDTO) {
        final FotoReporte fotoReporte = fotoReporteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fotoReporteDTO, fotoReporte);
        fotoReporteRepository.save(fotoReporte);
    }

    public void delete(final Long id) {
        final FotoReporte fotoReporte = fotoReporteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        fotoReporteRepository.delete(fotoReporte);
    }

    private FotoReporteDTO mapToDTO(final FotoReporte fotoReporte,
            final FotoReporteDTO fotoReporteDTO) {
        fotoReporteDTO.setId(fotoReporte.getId());
        fotoReporteDTO.setUrlFoto(fotoReporte.getUrlFoto());
        fotoReporteDTO.setCreatedAt(fotoReporte.getCreatedAt());
        fotoReporteDTO.setReporte(fotoReporte.getReporte() == null ? null : fotoReporte.getReporte().getId());
        return fotoReporteDTO;
    }

    private FotoReporte mapToEntity(final FotoReporteDTO fotoReporteDTO,
            final FotoReporte fotoReporte) {
        fotoReporte.setUrlFoto(fotoReporteDTO.getUrlFoto());
        fotoReporte.setCreatedAt(fotoReporteDTO.getCreatedAt());
        final Reporte reporte = fotoReporteDTO.getReporte() == null ? null : reporteRepository.findById(fotoReporteDTO.getReporte())
                .orElseThrow(() -> new NotFoundException("reporte not found"));
        fotoReporte.setReporte(reporte);
        return fotoReporte;
    }

    @EventListener(BeforeDeleteReporte.class)
    public void on(final BeforeDeleteReporte event) {
        final ReferencedException referencedException = new ReferencedException();
        final FotoReporte reporteFotoReporte = fotoReporteRepository.findFirstByReporteId(event.getId());
        if (reporteFotoReporte != null) {
            referencedException.setKey("reporte.fotoReporte.reporte.referenced");
            referencedException.addParam(reporteFotoReporte.getId());
            throw referencedException;
        }
    }

}
