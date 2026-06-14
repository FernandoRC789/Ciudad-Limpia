package io.ciudadlimpia.app.mapa;

import io.ciudadlimpia.app.security.JwtService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/mapa", produces = MediaType.APPLICATION_JSON_VALUE)
public class MapaController {

    private final MapaService mapaService;

    public MapaController(MapaService mapaService) {
        this.mapaService = mapaService;
    }

    @GetMapping("/reportes-activos")
    public ResponseEntity<List<ReporteActivoDTO>> getReportesActivos() {
        return ResponseEntity.ok(mapaService.getReportesActivos());
    }
}