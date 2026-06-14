package io.ciudadlimpia.app.usuario;

import io.ciudadlimpia.app.events.BeforeDeleteRol;
import io.ciudadlimpia.app.events.BeforeDeleteUsuario;
import io.ciudadlimpia.app.rol.Rol;
import io.ciudadlimpia.app.rol.RolRepository;
import io.ciudadlimpia.app.util.NotFoundException;
import io.ciudadlimpia.app.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ApplicationEventPublisher publisher;

    public UsuarioService(final UsuarioRepository usuarioRepository,
            final RolRepository rolRepository, final ApplicationEventPublisher publisher) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.publisher = publisher;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("id"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
    }

    public UsuarioDTO get(final Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UsuarioDTO usuarioDTO) {
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);
        return usuarioRepository.save(usuario).getId();
    }

    public void update(final Long id, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Long id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUsuario(id));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setDni(usuario.getDni());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setPasswordHash(usuario.getPasswordHash());
        usuarioDTO.setTelefono(usuario.getTelefono());
        usuarioDTO.setFotoPerfil(usuario.getFotoPerfil());
        usuarioDTO.setActivo(usuario.getActivo());
        usuarioDTO.setCreatedAt(usuario.getCreatedAt());
        usuarioDTO.setUpdatedAt(usuario.getUpdatedAt());
        usuarioDTO.setDireccion(usuario.getDireccion());
        usuarioDTO.setDistrito(usuario.getDistrito());
        usuarioDTO.setRol(usuario.getRol() == null ? null : usuario.getRol().getId());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setDni(usuarioDTO.getDni());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPasswordHash(usuarioDTO.getPasswordHash());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setFotoPerfil(usuarioDTO.getFotoPerfil());
        usuario.setActivo(usuarioDTO.getActivo());
        usuario.setCreatedAt(usuarioDTO.getCreatedAt());
        usuario.setUpdatedAt(usuarioDTO.getUpdatedAt());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setDistrito(usuarioDTO.getDistrito());
        final Rol rol = usuarioDTO.getRol() == null ? null
                : rolRepository.findById(usuarioDTO.getRol())
                        .orElseThrow(() -> new NotFoundException("rol not found"));
        usuario.setRol(rol);
        return usuario;
    }

    @EventListener(BeforeDeleteRol.class)
    public void on(final BeforeDeleteRol event) {
        final ReferencedException referencedException = new ReferencedException();
        final Usuario rolUsuario = usuarioRepository.findFirstByRolId(event.getId());
        if (rolUsuario != null) {
            referencedException.setKey("rol.usuario.rol.referenced");
            referencedException.addParam(rolUsuario.getId());
            throw referencedException;
        }
    }

}
