package io.ciudadlimpia.app.dashboard;

import io.ciudadlimpia.app.gaming_profile.RankingResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class DashboardResource {

    private final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenResponse> getResumen() {
        return ResponseEntity.ok(dashboardService.getResumen());
    }

    @GetMapping("/zonas-calor")
    public ResponseEntity<List<ZonaCalorResponse>> getZonasCalor() {
        return ResponseEntity.ok(dashboardService.getZonasCalor());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking() {
        return ResponseEntity.ok(dashboardService.getRanking());
    }
}