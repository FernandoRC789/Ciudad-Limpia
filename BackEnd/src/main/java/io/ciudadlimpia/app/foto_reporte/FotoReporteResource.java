package io.ciudadlimpia.app.foto_reporte;

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
@RequestMapping(value = "/api/fotoReportes", produces = MediaType.APPLICATION_JSON_VALUE)
public class FotoReporteResource {

    private final FotoReporteService fotoReporteService;

    public FotoReporteResource(final FotoReporteService fotoReporteService) {
        this.fotoReporteService = fotoReporteService;
    }

    @GetMapping
    public ResponseEntity<List<FotoReporteDTO>> getAllFotoReportes() {
        return ResponseEntity.ok(fotoReporteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoReporteDTO> getFotoReporte(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(fotoReporteService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createFotoReporte(
            @RequestBody @Valid final FotoReporteDTO fotoReporteDTO) {
        final Long createdId = fotoReporteService.create(fotoReporteDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateFotoReporte(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final FotoReporteDTO fotoReporteDTO) {
        fotoReporteService.update(id, fotoReporteDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFotoReporte(@PathVariable(name = "id") final Long id) {
        fotoReporteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
