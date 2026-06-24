package io.ciudadlimpia.app.perfil;


import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfileRepository;
import io.ciudadlimpia.app.nivel.Nivel;
import io.ciudadlimpia.app.nivel.NivelRepository;
import io.ciudadlimpia.app.reporte.ReporteRepository;
import io.ciudadlimpia.app.usuario.Usuario;
import io.ciudadlimpia.app.usuario.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final GamingProfileRepository gamingProfileRepository;
    private final ReporteRepository reporteRepository;
    private final NivelRepository nivelRepository;

    public PerfilResponse obtenerPerfil(Long usuarioId) {

        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow();

        GamingProfile profile =
                gamingProfileRepository
                        .findByUsuarioId(usuarioId)
                        .orElseThrow();

        Nivel nivelActual =
                profile.getNivel();

        Optional<Nivel> siguienteNivel =
                nivelRepository
                        .findFirstByPuntosMinimosGreaterThanOrderByPuntosMinimosAsc(
                                nivelActual.getPuntosMinimos()
                        );

        int totalReportes =
                reporteRepository.countByUsuarioId(usuarioId);

        int reportesAtendidos =
                reporteRepository.countByUsuarioIdAndEstado(
                        usuarioId,
                        "ATENDIDO"
                );

        Integer puntosParaSiguienteNivel =
                siguienteNivel
                        .map(Nivel::getPuntosMinimos)
                        .orElse(
                                nivelActual.getPuntosMinimos()
                        );

        PerfilResponse.NivelInfo nivelInfo =
                new PerfilResponse.NivelInfo(
                        profile.getNivel().getId(),
                        profile.getNivel().getNombre(),
                        profile.getNivel().getIcono(),
                        puntosParaSiguienteNivel
                );

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getFotoPerfil(),
                profile.getPuntosTotales(),
                nivelInfo,
                siguienteNivel
                        .map(Nivel::getNombre)
                        .orElse("Nivel Máximo"),
                totalReportes,
                reportesAtendidos
        );
    }

    @Transactional
    public void actualizarFoto(
            Long usuarioId,
            String fotoPerfil
    ) {

        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow();

        usuario.setFotoPerfil(fotoPerfil);

        usuarioRepository.save(usuario);
    }
}