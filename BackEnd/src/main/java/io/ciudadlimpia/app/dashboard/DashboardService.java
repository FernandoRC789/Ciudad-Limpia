package io.ciudadlimpia.app.dashboard;

import io.ciudadlimpia.app.gaming_profile.RankingResponse;
import io.ciudadlimpia.app.gaming_profile.GamingProfileService;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DashboardService {

    private final ReporteRepository reporteRepository;
    private final GamingProfileService gamingProfileService;

    public DashboardService(ReporteRepository reporteRepository,
                            GamingProfileService gamingProfileService) {
        this.reporteRepository = reporteRepository;
        this.gamingProfileService = gamingProfileService;
    }

    public DashboardResumenResponse getResumen() {
        long total      = reporteRepository.count();
        long pendientes = reporteRepository.countByEstado("PENDIENTE");
        long enRevision = reporteRepository.countByEstado("EN_REVISION");
        long atendidos  = reporteRepository.countByEstado("ATENDIDO");
        long rechazados = reporteRepository.countByEstado("RECHAZADO");

        // Reportes creados hoy
        OffsetDateTime inicioDia = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay().atOffset(ZoneOffset.UTC);
        long hoy = reporteRepository.countByCreatedAtAfter(inicioDia);

        return new DashboardResumenResponse(total, pendientes, enRevision, atendidos, rechazados, hoy);
    }

    public List<ZonaCalorResponse> getZonasCalor() {
        return reporteRepository.getZonasCalor();
    }

    public List<RankingResponse> getRanking() {
        return gamingProfileService.getRanking();
    }
}