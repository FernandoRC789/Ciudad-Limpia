package io.ciudadlimpia.app.canje;

import io.ciudadlimpia.app.security.JwtService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/canjes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CanjeResource {

    private final CanjeService canjeService;
    private final JwtService jwtService;

    public CanjeResource(CanjeService canjeService, JwtService jwtService) {
        this.canjeService = canjeService;
        this.jwtService = jwtService;
    }

    // CIUDADANO — canjear cupón
    @PostMapping("/cupon/{cuponId}")
    public ResponseEntity<CanjeResponse> canjear(
            @PathVariable Integer cuponId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return new ResponseEntity<>(canjeService.canjear(cuponId, usuarioId), HttpStatus.CREATED);
    }

    // CIUDADANO — ver mis canjes
    @GetMapping("/mis-canjes")
    public ResponseEntity<List<CanjeResponse>> getMisCanjes(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return ResponseEntity.ok(canjeService.getMisCanjes(usuarioId));
    }

    private Long extractUsuarioId(String authHeader) {
        return jwtService.extractUsuarioId(authHeader.substring(7));
    }
}