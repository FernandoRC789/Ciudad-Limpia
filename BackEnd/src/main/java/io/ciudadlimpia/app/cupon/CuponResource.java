package io.ciudadlimpia.app.cupon;

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
@RequestMapping(value = "/api/cupons", produces = MediaType.APPLICATION_JSON_VALUE)
public class CuponResource {

    private final CuponService cuponService;

    public CuponResource(final CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @GetMapping
    public ResponseEntity<List<CuponDTO>> getAllCupons() {
        return ResponseEntity.ok(cuponService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuponDTO> getCupon(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(cuponService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createCupon(@RequestBody @Valid final CuponDTO cuponDTO) {
        final Integer createdId = cuponService.create(cuponDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateCupon(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final CuponDTO cuponDTO) {
        cuponService.update(id, cuponDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCupon(@PathVariable(name = "id") final Integer id) {
        cuponService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Ciudadano — ver cupones disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<CuponDTO>> getCuponesDisponibles() {
        return ResponseEntity.ok(cuponService.findDisponibles());
    }

}
