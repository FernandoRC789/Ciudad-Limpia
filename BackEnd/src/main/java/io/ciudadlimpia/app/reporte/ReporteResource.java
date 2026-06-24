package io.ciudadlimpia.app.reporte;

import io.ciudadlimpia.app.security.JwtService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/reportes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReporteResource {
    private final ReporteService reporteService;
    private final JwtService jwtService;

    public ReporteResource(ReporteService reporteService, JwtService jwtService) {
        this.reporteService = reporteService;
        this.jwtService = jwtService;
    }

    // ADMIN — listar todos
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> getAllReportes() {
        return ResponseEntity.ok(reporteService.findAll());
    }

    // CIUDADANO — ver mis reportes
    @GetMapping("/mis-reportes")
    public ResponseEntity<List<ReporteDTO>> getMisReportes(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return ResponseEntity.ok(reporteService.findByUsuario(usuarioId));
    }

    // CIUDADANO — crear reporte
    @PostMapping
    public ResponseEntity<ReporteDTO> createReporte(
            @RequestBody @Valid CrearReporteRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return new ResponseEntity<>(
                reporteService.createForCiudadano(request, usuarioId),
                HttpStatus.CREATED
        );
    }

    // ADMIN — cambiar estado individual
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long adminId = extractUsuarioId(authHeader);
        reporteService.cambiarEstado(id, request.getEstado(), request.getComentario(), adminId);
        return ResponseEntity.ok().build();
    }

    // ADMIN — cambiar estado en bloque
    @PatchMapping("/estado")
    public ResponseEntity<CambiarEstadoBloqueResponse> cambiarEstadoEnBloque(
            @RequestBody CambiarEstadoBloqueRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long adminId = extractUsuarioId(authHeader);
        int actualizados = reporteService.cambiarEstadoEnBloque(
                request.getIds(), request.getEstado(), request.getComentario(), adminId);
        return ResponseEntity.ok(new CambiarEstadoBloqueResponse(actualizados, request.getEstado()));
    }

    // Extrae el usuarioId del token JWT
    private Long extractUsuarioId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractUsuarioId(token);
    }
}