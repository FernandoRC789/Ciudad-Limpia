package io.ciudadlimpia.app.historial_estado;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/historialEstados", produces = MediaType.APPLICATION_JSON_VALUE)
public class HistorialEstadoResource {

    private final HistorialEstadoService historialEstadoService;

    public HistorialEstadoResource(final HistorialEstadoService historialEstadoService) {
        this.historialEstadoService = historialEstadoService;
    }

    @GetMapping
    public ResponseEntity<List<HistorialEstadoDTO>> getAllHistorialEstados() {
        return ResponseEntity.ok(historialEstadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstadoDTO> getHistorialEstado(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(historialEstadoService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createHistorialEstado(
            @RequestBody @Valid final HistorialEstadoDTO historialEstadoDTO) {
        final Long createdId = historialEstadoService.create(historialEstadoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateHistorialEstado(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final HistorialEstadoDTO historialEstadoDTO) {
        historialEstadoService.update(id, historialEstadoDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistorialEstado(@PathVariable(name = "id") final Long id) {
        historialEstadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
