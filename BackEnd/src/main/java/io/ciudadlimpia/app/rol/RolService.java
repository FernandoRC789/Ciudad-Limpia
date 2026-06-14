package io.ciudadlimpia.app.rol;

import io.ciudadlimpia.app.events.BeforeDeleteRol;
import io.ciudadlimpia.app.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class RolService {

    private final RolRepository rolRepository;
    private final ApplicationEventPublisher publisher;

    public RolService(final RolRepository rolRepository,
            final ApplicationEventPublisher publisher) {
        this.rolRepository = rolRepository;
        this.publisher = publisher;
    }

    public List<RolDTO> findAll() {
        final List<Rol> rols = rolRepository.findAll(Sort.by("id"));
        return rols.stream()
                .map(rol -> mapToDTO(rol, new RolDTO()))
                .toList();
    }

    public RolDTO get(final Integer id) {
        return rolRepository.findById(id)
                .map(rol -> mapToDTO(rol, new RolDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final RolDTO rolDTO) {
        final Rol rol = new Rol();
        mapToEntity(rolDTO, rol);
        return rolRepository.save(rol).getId();
    }

    public void update(final Integer id, final RolDTO rolDTO) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(rolDTO, rol);
        rolRepository.save(rol);
    }

    public void delete(final Integer id) {
        final Rol rol = rolRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteRol(id));
        rolRepository.delete(rol);
    }

    private RolDTO mapToDTO(final Rol rol, final RolDTO rolDTO) {
        rolDTO.setId(rol.getId());
        rolDTO.setNombre(rol.getNombre());
        rolDTO.setDescripcion(rol.getDescripcion());
        return rolDTO;
    }

    private Rol mapToEntity(final RolDTO rolDTO, final Rol rol) {
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());
        return rol;
    }

}
