package io.ciudadlimpia.app.nivel;

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
@RequestMapping(value = "/api/nivels", produces = MediaType.APPLICATION_JSON_VALUE)
public class NivelResource {

    private final NivelService nivelService;

    public NivelResource(final NivelService nivelService) {
        this.nivelService = nivelService;
    }

    @GetMapping
    public ResponseEntity<List<NivelDTO>> getAllNivels() {
        return ResponseEntity.ok(nivelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NivelDTO> getNivel(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(nivelService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createNivel(@RequestBody @Valid final NivelDTO nivelDTO) {
        final Integer createdId = nivelService.create(nivelDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateNivel(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final NivelDTO nivelDTO) {
        nivelService.update(id, nivelDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNivel(@PathVariable(name = "id") final Integer id) {
        nivelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
