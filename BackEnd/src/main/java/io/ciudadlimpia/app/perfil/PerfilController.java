package io.ciudadlimpia.app.perfil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<PerfilResponse> obtenerPerfil(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                perfilService.obtenerPerfil(usuarioId)
        );
    }

    @PutMapping("/{usuarioId}/foto")
    public ResponseEntity<Void> actualizarFoto(
            @PathVariable Long usuarioId,
            @RequestBody String fotoPerfil
    ) {

        perfilService.actualizarFoto(
                usuarioId,
                fotoPerfil
        );

        return ResponseEntity.ok().build();
    }
}
