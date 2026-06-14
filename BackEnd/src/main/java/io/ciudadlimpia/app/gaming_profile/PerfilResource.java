package io.ciudadlimpia.app.gaming_profile;

import io.ciudadlimpia.app.gaming_profile_badge.GamingProfileBadgeDTO;
import io.ciudadlimpia.app.security.JwtService;
import io.ciudadlimpia.app.transaccion_puntos.TransaccionPuntosDTO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/api/perfil", produces = MediaType.APPLICATION_JSON_VALUE)
public class PerfilResource {

    private final GamingProfileService gamingProfileService;
    private final JwtService jwtService;

    public PerfilResource(GamingProfileService gamingProfileService, JwtService jwtService) {
        this.gamingProfileService = gamingProfileService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<PerfilResponse> getMiPerfil(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return ResponseEntity.ok(gamingProfileService.getPerfil(usuarioId));
    }

    @GetMapping("/badges")
    public ResponseEntity<List<GamingProfileBadgeDTO>> getMisBadges(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return ResponseEntity.ok(gamingProfileService.getMisBadges(usuarioId));
    }

    private Long extractUsuarioId(String authHeader) {
        return jwtService.extractUsuarioId(authHeader.substring(7));
    }

    @GetMapping("/puntos/historial")
    public ResponseEntity<List<TransaccionPuntosDTO>> getHistorialPuntos(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extractUsuarioId(authHeader);
        return ResponseEntity.ok(gamingProfileService.getHistorialPuntos(usuarioId));
    }
}