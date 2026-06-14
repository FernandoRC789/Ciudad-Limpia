package io.ciudadlimpia.app.transaccion_puntos;

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
@RequestMapping(value = "/api/transaccionPuntoss", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransaccionPuntosResource {

    private final TransaccionPuntosService transaccionPuntosService;

    public TransaccionPuntosResource(final TransaccionPuntosService transaccionPuntosService) {
        this.transaccionPuntosService = transaccionPuntosService;
    }

    @GetMapping
    public ResponseEntity<List<TransaccionPuntosDTO>> getAllTransaccionPuntoss() {
        return ResponseEntity.ok(transaccionPuntosService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionPuntosDTO> getTransaccionPuntos(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(transaccionPuntosService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createTransaccionPuntos(
            @RequestBody @Valid final TransaccionPuntosDTO transaccionPuntosDTO) {
        final Long createdId = transaccionPuntosService.create(transaccionPuntosDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTransaccionPuntos(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TransaccionPuntosDTO transaccionPuntosDTO) {
        transaccionPuntosService.update(id, transaccionPuntosDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaccionPuntos(@PathVariable(name = "id") final Long id) {
        transaccionPuntosService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
